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

import fi.capeismi.fish.uistelupaivakirja.web.model.AnnotatedView;
import fi.capeismi.fish.uistelupaivakirja.web.model.FillableAnnotation;
import fi.capeismi.fish.uistelupaivakirja.web.model.TableView;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
class Adapter {
    protected RestfulModel model;
    
    public AnnotatedView decorate(TableView view) {
        Properties props = new Properties();
        try {
            ApplicationContext ctx = new ClassPathXmlApplicationContext();

            props.load(new FileInputStream(ctx.getResource("classpath:fi/capeismi/fish/uistelupaivakirja/web/controller/views.properties").getFile()));
            String classname = props.getProperty(view.getName());
            if(classname == null) {
                return null;
            }            
            FillableAnnotation fillable = (FillableAnnotation)Class.forName(classname).newInstance();

            for(Map<String,String> row: view.getTable()) {
                fillable.add(row);
            }
            
            return fillable;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
