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

import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObject;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObjectCollection;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class XMLCreator {

    private TrollingObjectCollection objects;
    
    public XMLCreator()
    {
        
    }
    
    public XMLCreator(TrollingObjectCollection objects)
    {
        this.objects = objects;
    }
    
    public DOMSource getSource()
    {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("TrollingObjects");            
            doc.appendChild(root);
            int revision = this.objects.getRevision().getId().intValue();
            int maxId = 0;
            
            for(TrollingObject trollingobject: this.objects.getObjects())
            {
                Element object = doc.createElement("TrollingObject");
                object.setAttribute("type", trollingobject.getType().name());
                object.setAttribute("id", new Integer(trollingobject.getId()).toString());
                createTrollingObject(doc, object, trollingobject);
                root.appendChild(object);
                maxId = trollingobject.getId();
            }
                  
            root.setAttribute("revision", new Integer(revision).toString());
            root.setAttribute("MaxId", new Integer(maxId).toString());
            return new DOMSource(doc);
            
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return null;
    }
    
    private void createTrollingObject(Document doc, Element element, TrollingObject object)
    {
        Map<String, String> kvs = object.getKeyvalues();
        for(String key: kvs.keySet())
        {
            Element keyvalue = doc.createElement(key);
            String value = kvs.get(key);
            if(value == null)
                continue;
            
            keyvalue.appendChild(doc.createTextNode(value));
            element.appendChild(keyvalue);
        }
        
        List<Map<String, String>> props = object.getPropertylist();
        Element proplist = doc.createElement("PropertyList");
        for(Map<String, String> prop: props)
        {
            Element item = doc.createElement("PropertyListItem");
            for(Map.Entry<String, String> kv: prop.entrySet())
            {
                Element kvpair = doc.createElement(kv.getKey());
                kvpair.appendChild(doc.createTextNode(kv.getValue()));
                item.appendChild(kvpair);
            }
            proplist.appendChild(item);
        }
        
        if(proplist.hasChildNodes())
        {
            element.appendChild(proplist);
        }
        
    }
    
}
