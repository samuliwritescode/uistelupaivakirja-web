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
import fi.capeismi.fish.uistelupaivakirja.web.dao.Event;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Eventproperty;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Property;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;

import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class XMLCreator {
    
    
    public static DOMSource marshal(AnnotatedView object) {        
        ViewMarshaller marshaller = new ViewMarshaller(object);
        return marshaller.getSource();
    }

    public static DOMSource marshal(Collection collection) {
        CollectionMarshaller marshaller = new CollectionMarshaller(collection);
        return marshaller.getSource();
    }
  
    private static class ViewMarshaller{
        private DOMSource _domsource = null;
        
        public ViewMarshaller(AnnotatedView object) {
            try {
                JAXBContext ctx = JAXBContext.newInstance(object.getClass());
                Marshaller m = ctx.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                m.marshal(object, node);
                this._domsource = new DOMSource(node);
            } catch (Exception ex) {
                throw new RestfulException(ex.toString());
            } 
        }
        
        public DOMSource getSource()
        {
            return this._domsource;
        }
    }

    private static class CollectionMarshaller {
        private DOMSource _domsource = null;
        
        public CollectionMarshaller(Collection collection) {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element root = doc.createElement("TrollingObjects");            
                doc.appendChild(root);
                int revision = collection.getRevision();
                int maxId = 0;

                for(Trollingobject trollingobject: collection.getTrollingobjects())
                {
                    Element object = doc.createElement("TrollingObject");
                    object.setAttribute("type", collection.getType().getName());
                    object.setAttribute("id", new Integer(trollingobject.getObjectIdentifier()).toString());
                    createTrollingObject(doc, object, trollingobject);
                    root.appendChild(object);
                    maxId = trollingobject.getObjectIdentifier();
                }

                root.setAttribute("revision", new Integer(revision).toString());
                root.setAttribute("MaxId", new Integer(maxId).toString());
                this._domsource = new DOMSource(doc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        private void createTrollingObject(Document doc, Element element, Trollingobject object)
        {            
            Set<Property> kvs = object.getProperties();
            for(Property prop: kvs)
            {
                Element keyvalue = doc.createElement(prop.getKeyvalue().getKeyname());
                String value = prop.getKeyvalue().getValue();
                if(value == null)
                    continue;

                keyvalue.appendChild(doc.createTextNode(value));
                element.appendChild(keyvalue);
            }

            Element proplist = doc.createElement("PropertyList");
            for(Event event: object.getEvents())
            {
                Element item = doc.createElement("PropertyListItem");
                createPropertyItem(doc, item, event);
                proplist.appendChild(item);
            }

            if(proplist.hasChildNodes())
            {
                element.appendChild(proplist);
            }

        }

        private void createPropertyItem(Document doc, Element item, Event event) {
            for(Eventproperty prop: event.getEventproperties())
            {
                Element kvpair = doc.createElement(prop.getKeyvalue().getKeyname());
                kvpair.appendChild(doc.createTextNode(prop.getKeyvalue().getValue()));
                item.appendChild(kvpair);
            }
        }
        
        public DOMSource getSource()
        {
           return this._domsource;
        }
    }
    
}
