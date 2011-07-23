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

import fi.capeismi.fish.uistelupaivakirja.web.model.Factory;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingEvent;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObject;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObjectCollection;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class XMLReader {
    TrollingObjectCollection _collection = null;
    
    public XMLReader(InputStream in) throws ParserConfigurationException, SAXException, IOException
    {
        _collection = Factory.createEmptyCollection();
        parseDocument(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in));
    }
    
    private void parseDocument(Document doc) {
        Element root = doc.getDocumentElement();
        if(root.getNodeName().equalsIgnoreCase("TrollingObjects"))
        {
            readTrollingObjectCollection(root);
        }  
    }
    
    
    private void readTrollingObjectCollection(Element collection) {
        String revision = collection.getAttribute("revision");
        //String type = collection.getAttribute("type");
        if(revision.isEmpty())
        {
            revision = "0";
        }
        
        _collection.setRevision(new Integer(revision).intValue());
        

        NodeList children = collection.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++)                
        {
            Node node = children.item(loop);
            if(node.getNodeName().equalsIgnoreCase("TrollingObject"))
            {
                readTrollingObject((Element)node);
            }
        }
    }
    
    private void readTrollingObject(Element trollingObjectElement)
    {
        String id = trollingObjectElement.getAttribute("id");
        TrollingObject object = Factory.createEmptyTrollingObject();
        _collection.addTrollingObject(object);
        object.setId(new Integer(id).intValue());
        
        NodeList children = trollingObjectElement.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++)
        {
            Node node = children.item(loop);
            if(node.getNodeName().equalsIgnoreCase("PropertyList"))
            {
                readPropertyList(object, node);
            }else if(node.getNodeType() == Node.ELEMENT_NODE)
            {                               
                String value = node.getChildNodes().item(0).getNodeValue();

                if(value == null)
                    value = "";
                object.setKeyValue(node.getNodeName(), value);
            }
        }                
    }
    
    private void readPropertyList(TrollingObject object, Node propertylist) 
    {        
        NodeList children = propertylist.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++) 
        {
            Node node = children.item(loop);
            if(node.getNodeName().equalsIgnoreCase("PropertyListItem"))
            {
                TrollingEvent event = Factory.createEmptyTrollingEvent();
                object.addEvent(event);
                readPropertyListItem(event, node);
            }
        }
    }
    
    private void readPropertyListItem(TrollingEvent event, Node listitem)
    {
        NodeList children = listitem.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++)
        {
            Node node = children.item(loop);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes())
            {
                String value = node.getChildNodes().item(0).getNodeValue();
                if(value == null)
                    value = "";
                String key = node.getNodeName();
                
                event.setKeyValue(key, value);
            }
        }
    }
    
    public TrollingObjectCollection getTrollingObjects()
    {
        return this._collection;
    }
    
}
