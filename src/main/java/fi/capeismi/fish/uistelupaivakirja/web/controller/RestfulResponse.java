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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class RestfulResponse {
    private String _contentType;
    private String _content;
    
    public static DOMSource getResponse(String responsetype, String content)
    {
        RestfulResponse retval = new RestfulResponse(responsetype, content);
        return retval.getBody();
    }
    
    public RestfulResponse(String responsetype, String content) {
        this._contentType = responsetype;
        this._content = content;
    }
    
    public RestfulResponse(String responsetype) {
        this._contentType = responsetype;
    }
    
    public void setContent(String response) {
        this._content = response;
    }
    
    @Override
    public String toString() {
        return _content;
    }
    
    public DOMSource getBody() {
        try {            
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement(this._contentType);
            Node textnode = doc.createTextNode(this._content);
            root.appendChild(textnode);
            doc.appendChild(root);
            return new DOMSource(doc);
        }
        catch(Exception e)
        {

        }
        return null;
    }
    
}
