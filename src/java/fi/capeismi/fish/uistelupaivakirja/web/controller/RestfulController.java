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

import fi.capeismi.fish.uistelupaivakirja.web.dao.AnnotatedView;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Collection;
import fi.capeismi.fish.uistelupaivakirja.web.dao.TableView;
import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;

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
@RequestMapping("/api")
public class RestfulController 
{
    private LoginService m_loginService = null;
    
    private static final String RESPONSE_EXCEPTION = "TrollingException";
    private static final String RESPONSE_TRANSACTIONTICKET = "TransactionTicket";
    private static final String RESPONSE_RESPONSE = "TrollingResponse";     
    
    @RequestMapping(value="/{collection}", method=RequestMethod.GET)
    @ResponseBody
    public DOMSource getItems(@PathVariable String collection) 
    {                        
        RestfulModel model = m_loginService.getModel();

        return XMLCreator.marshal(model.getTrollingObjects(collection));        
    }
    
    @RequestMapping(value="/views/{view}", method=RequestMethod.GET)
    @ResponseBody
    public DOMSource getView(@PathVariable String view) 
    {                        
        RestfulModel model = m_loginService.getModel();
        TableView viewobject = model.getView(view);
        AnnotatedView annotated = new Adapter().decorate(viewobject);
        if(annotated == null)
            return XMLCreator.marshal(viewobject);
        else
            return XMLCreator.marshal(annotated);
    }
    
    @RequestMapping(value="/{doctype}", method=RequestMethod.POST)
    @ResponseBody
    public DOMSource postItems(
        @RequestBody String content, 
        @PathVariable String doctype)
    {
        return putOrPostTrips(content, doctype, true);
    }
    
    @RequestMapping(value="/{doctype}", method=RequestMethod.PUT)
    @ResponseBody
    public DOMSource putItems(
        @RequestBody String content, 
        @PathVariable String doctype)
    {
        return putOrPostTrips(content, doctype, false);
    }
    
    private DOMSource putOrPostTrips(String content, String doctype, boolean append)
    {
       
        RestfulModel model = m_loginService.getModel();
        RestfulResponse response = new RestfulResponse(RESPONSE_TRANSACTIONTICKET);
        try {            
            InputStream in = stringToInputStream(content);
            XMLReader reader = new XMLReader(in);
            Collection objects = reader.getTrollingObjects();
            objects.setType(model.getType(doctype));
                        
            Integer commitId = null;
            if(append)
            {
                commitId = model.appendTrollingObjects(objects);
            }else
            {
               commitId = model.setTrollingObjects(objects); 
            }
            
            response.setContent(commitId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RestfulException(e.toString());
        }
        
        return response.getBody();
    }
    
    public static InputStream stringToInputStream(String content) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(content.getBytes("ISO-8859-1"));
    }
    
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RestfulException.class)
    @ResponseBody
    public DOMSource handleException(RestfulException ex) 
    {
        return RestfulResponse.getResponse(RESPONSE_EXCEPTION, ex.toString());             
    }
    
    @RequestMapping(value="/userinfo", method=RequestMethod.GET)
    @ResponseBody
    public DOMSource userinfo() {
        RestfulModel model = m_loginService.getModel();        
        return XMLCreator.marshal(model.getUser());
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
    

}
