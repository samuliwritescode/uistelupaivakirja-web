/*
 * Copyright (C) 2011 Samuli Penttilä <samuli.penttila@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fi.capeismi.fish.uistelupaivakirja.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObjectCollection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.transform.dom.DOMSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@Controller
@RequestMapping("/")
public class RestfulController 
{
    private LoginService m_loginService = null;
    
    private static final String RESPONSE_EXCEPTION = "TrollingException";
    private static final String RESPONSE_TRANSACTIONTICKET = "TransactionTicket";
    private static final String RESPONSE_RESPONSE = "TrollingResponse";
      
    @RequestMapping(value="/{doctype}", method=RequestMethod.GET)
    @ResponseBody
    public DOMSource getPlaces(@PathVariable String doctype) 
    {                
        RestfulModel.EType type = parseTypes(doctype);
        RestfulModel model = m_loginService.getModel();
        TrollingObjectCollection list = model.getTrollingObjects(type);
        XMLCreator creator = new XMLCreator(list);

        return creator.getSource();   
    }

    @RequestMapping(value="/{doctype}", method=RequestMethod.POST)
    @ResponseBody
    public DOMSource postTrips(
        @RequestBody String content, 
        @PathVariable String doctype)
    {
        return putOrPostTrips(content, doctype, true);
    }
    
    @RequestMapping(value="/{doctype}", method=RequestMethod.PUT)
    @ResponseBody
    public DOMSource putTrips(
        @RequestBody String content, 
        @PathVariable String doctype)
    {
        return putOrPostTrips(content, doctype, false);
    }
    
    private DOMSource putOrPostTrips(String content, String doctype, boolean append)
    {
        RestfulModel.EType type = parseTypes(doctype);
        
        RestfulModel model = m_loginService.getModel();
        RestfulResponse response = new RestfulResponse(RESPONSE_TRANSACTIONTICKET);
        try {            
            InputStream in = new ByteArrayInputStream(content.getBytes("ISO-8859-1"));            
            XMLReader reader = new XMLReader(model, in);
            TrollingObjectCollection objects = reader.getTrollingObjects();
                        
            Integer commitId = null;
            if(append)
            {
                commitId = model.appendTrollingObjects(type, objects);
            }else
            {
               commitId = model.setTrollingObjects(type, objects); 
            }
            
            response.setContent(commitId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestfulException(e.toString());
        }
        
        return response.getBody();
    }
    
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RestfulException.class)
    @ResponseBody
    public DOMSource handleException(RestfulException ex) 
    {
        return RestfulResponse.getResponse(RESPONSE_EXCEPTION, ex.toString());             
    }
    
    @RequestMapping(value="/logout", method= RequestMethod.GET)
    @ResponseBody
    public DOMSource logout()
    {
        m_loginService.logout();
        return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");       
    }
    
    
    @RequestMapping(value="/login", method=RequestMethod.GET)
    @ResponseBody
    public DOMSource login(
        @RequestParam String j_username, 
        @RequestParam String j_password)
    {                
        m_loginService.login(j_username, j_password);
        return RestfulResponse.getResponse(RESPONSE_RESPONSE, "OK");            
    }
    
    @Autowired
    public void setLoginService(LoginService service) 
    {
        m_loginService = service;
    }
    
    private static RestfulModel.EType parseTypes(String type)
    {
        if(type.equalsIgnoreCase("trips"))
        {
            return RestfulModel.EType.trip;
        }
        else if(type.equalsIgnoreCase("places"))
        {
            return RestfulModel.EType.place;
        }
        else if(type.equalsIgnoreCase("lures"))
        {
            return RestfulModel.EType.lure;
        }
        else
        {
            return RestfulModel.EType.unknown;
        }
    }

}
