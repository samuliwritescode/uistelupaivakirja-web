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

import fi.capeismi.fish.uistelupaivakirja.web.dao.Revision;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObject;
import fi.capeismi.fish.uistelupaivakirja.web.model.TrollingObjectCollection;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    private TrollingObjectCollection objects = new TrollingObjectCollection();
    private RestfulModel model = null;
    
    public XMLReader(RestfulModel model, InputStream in) throws ParserConfigurationException, SAXException, IOException
    {
        this.model = model;
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        Element root = doc.getDocumentElement();
        if(root.getNodeName().equalsIgnoreCase("TrollingObjects"))
        {
            String revision = root.getAttribute("revision");
            if(revision.isEmpty())
            {
                revision = "0";
            }
            
            Revision revob = new Revision();
            revob.setId(new Integer(revision).intValue());            
            this.objects.setRevision(revob);
            
            NodeList children = root.getChildNodes();
            for(int loop=0; loop < children.getLength(); loop++)                
            {
                Node node = children.item(loop);
                if(node.getNodeName().equalsIgnoreCase("TrollingObject"))
                {
                    readTrollingObject((Element)node);
                }
            }
            
        }        
    }
    
    private void readTrollingObject(Element element)
    {
        String typestring = element.getAttribute("type");
        String id = element.getAttribute("id");
        
        RestfulModel.EType type = RestfulModel.EType.unknown;
        
        if(typestring.equals("trip"))
            type = RestfulModel.EType.trip;
        else if(typestring.equals("lure"))
            type = RestfulModel.EType.lure;
        else if(typestring.equals("place"))
            type = RestfulModel.EType.place;
        
        TrollingObject object = this.model.newTrollingObject(type, 
                new Integer(id).intValue());
        this.objects.addTrollingObject(object);
        
        NodeList children = element.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++)
        {
            Node node = children.item(loop);
            if(node.getNodeName().equalsIgnoreCase("PropertyList"))
            {
                object.addPropertyKeyValue(readPropertyList(node));
            }else if(node.getNodeType() == Node.ELEMENT_NODE)
            {                               
                String value = node.getChildNodes().item(0).getNodeValue();
                //System.out.println("name="+node.getNodeName()+", value="+value);
                if(value == null)
                    value = "";
                object.setKeyValue(node.getNodeName(), value);
            }
            else
            {
               // System.out.println("unknown node: "+node.getNodeValue());
            }
        }                
    }
    
    private List<Map<String, String>> readPropertyList(Node propertylist) 
    {
        List<Map<String, String>> retval = new ArrayList<Map<String, String>>();
        NodeList children = propertylist.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++) 
        {
            Node node = children.item(loop);
            if(node.getNodeName().equalsIgnoreCase("PropertyListItem"))
            {
                retval.add(readPropertyListItem(node));
            }
        }
        return retval;
    }
    
    private Map<String, String> readPropertyListItem(Node item)
    {
        Map<String, String> retval = new HashMap<String, String>();
        NodeList children = item.getChildNodes();
        for(int loop=0; loop < children.getLength(); loop++)
        {
            Node node = children.item(loop);
            if(node.getNodeType() == Node.ELEMENT_NODE && node.hasChildNodes())
            {
                String value = node.getChildNodes().item(0).getNodeValue();
                if(value == null)
                    value = "";
                String key = node.getNodeName();
                
                retval.put(key, value);
            }
        }
        return retval;
    }
    
    public TrollingObjectCollection getTrollingObjects()
    {
        return this.objects;
    }
    
}
