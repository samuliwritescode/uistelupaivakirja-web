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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@Controller
@RequestMapping("/")
public class UIController {
    
    private LoginService loginservice;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public ModelAndView getMainPage()
    {
        ModelAndView view = new ModelAndView();
        view.setViewName("index");
        return view;
    }
    
    @RequestMapping(value="/myfish", method=RequestMethod.GET)
    public ModelAndView getMyFish()
    {
        ModelAndView view = new ModelAndView();
        view.setViewName("myfish");
        return view;
    }
    
    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public ModelAndView getProfile()
    {
        ModelAndView view = new ModelAndView();
        view.setViewName("profile");
        return view;
    }
    
    @Autowired
    public void setLoginService(LoginService service) 
    {
        this.loginservice = service;
    }
}
