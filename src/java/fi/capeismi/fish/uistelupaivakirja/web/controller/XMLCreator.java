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

import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingEvent;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObject;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObjectCollection;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class XMLCreator {
    
    
        public static DOMSource marshal(Object object) {        
            if(object instanceof TrollingObjectCollection) {
                TrollingObjectCollection collection = (TrollingObjectCollection)object;
                TrollingCollectionMarshaller marshaller = new TrollingCollectionMarshaller(collection);
                return marshaller.getSource();
            }else
            {
                try {
                    JAXBContext ctx = JAXBContext.newInstance(object.getClass());
                    Marshaller m = ctx.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    Node node = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                    m.marshal(object, node);
                    return new DOMSource(node);
                } catch (Exception ex) {
                    throw new RestfulException(ex.toString());
                }
            }
    }
    

    private static class TrollingCollectionMarshaller {
        private DOMSource _domsource = null;
        
        public TrollingCollectionMarshaller(TrollingObjectCollection collection) {
            try {
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element root = doc.createElement("TrollingObjects");            
                doc.appendChild(root);
                int revision = collection.getRevision();
                int maxId = 0;

                for(TrollingObject trollingobject: collection.getObjects())
                {
                    Element object = doc.createElement("TrollingObject");
                    object.setAttribute("type", collection.getType());
                    object.setAttribute("id", new Integer(trollingobject.getId()).toString());
                    createTrollingObject(doc, object, trollingobject);
                    root.appendChild(object);
                    maxId = trollingobject.getId();
                }

                root.setAttribute("revision", new Integer(revision).toString());
                root.setAttribute("MaxId", new Integer(maxId).toString());
                this._domsource = new DOMSource(doc);

            } catch (Exception e) {
                e.printStackTrace();
            }
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

            List<TrollingEvent> events = object.getEvents();
            Element proplist = doc.createElement("PropertyList");
            for(TrollingEvent event: events)
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

        private void createPropertyItem(Document doc, Element item, TrollingEvent event) {
            for(Map.Entry<String, String> kv: event.getKeyvalues().entrySet())
            {
                Element kvpair = doc.createElement(kv.getKey());
                kvpair.appendChild(doc.createTextNode(kv.getValue()));
                item.appendChild(kvpair);
            }
        }
        
       public DOMSource getSource()
       {
           return this._domsource;
       }
    }
    
}
