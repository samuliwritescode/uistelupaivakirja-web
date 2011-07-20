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
package fi.capeismi.fish.uistelupaivakirja.web.model;

import fi.capeismi.fish.uistelupaivakirja.web.dao.Keyvalue;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Propertylist;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Propertylistitem;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Revision;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel.EType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class TrollingObject {
    private Trollingobject trollingobjectDAO = null;  
   
    public TrollingObject(Trollingobject dao)
    {                
        this.trollingobjectDAO = dao; 
        /*Revision revobj = new Revision();
        revobj.setId(new Integer(revision));
        this.trollingobjectDAO.setRevision(revobj);*/
    }
    
    public Trollingobject getDAO()
    {
        return this.trollingobjectDAO;
    }

    public int getId() {
        return this.trollingobjectDAO.getObjectIdentifier();
    }
    
    public EType getType()
    {        
        return EType.valueOf(this.trollingobjectDAO.getType());
    }

    public Map<String, String> getKeyvalues() {
       Map<String, String> retval = new HashMap<String, String>();
       Set keyvalues = this.trollingobjectDAO.getKeyvalues();
       for(Object o: keyvalues)
       {
           Keyvalue kv = (Keyvalue)o;
           retval.put(kv.getKeyname(), kv.getValue());
       }
       return retval;
    }

    public List<Map<String, String>> getPropertylist() {
        List<Map<String, String>> retval = new ArrayList<Map<String, String>>();
        Set propertylist = this.trollingobjectDAO.getPropertylists();
        for(Object o: propertylist)
        {
            Propertylist pl = (Propertylist)o;
            Set propertylistitems = pl.getPropertylistitems();
            Map<String, String> properties = new HashMap<String, String>();
            for(Object p: propertylistitems)
            {
                Propertylistitem item = (Propertylistitem)p;
                properties.put(item.getKeyname(), item.getValue());
            }
            retval.add(properties);
        }
        
        return retval;      
    }

    /*public int getRevision() {
        return this.trollingobjectDAO.getRevision().getId().intValue();
    } */   
    
    public void setKeyValue(String key, String value)
    {
        for(Object o: this.trollingobjectDAO.getKeyvalues())
        {
            Keyvalue kv = (Keyvalue)o;
            if(kv.getKeyname().equalsIgnoreCase(key))
            {
                kv.setValue(value);
                return;
            }
        }
        
        Keyvalue kv = new Keyvalue();
        kv.setKeyname(key);
        kv.setValue(value);
        kv.setTrollingobject(trollingobjectDAO);
        this.trollingobjectDAO.getKeyvalues().add(kv);
    }
    
    public void addPropertyKeyValue(List<Map<String, String>> properties)
    {          
        Set allprops = new LinkedHashSet();
        
        for(Map<String, String> o: properties)
        {
            Propertylist proplistitem = new Propertylist();
            proplistitem.setTrollingobject(this.trollingobjectDAO);
            Set items = new HashSet();
            for(Map.Entry<String, String> e: o.entrySet())
            {
                Propertylistitem item = new Propertylistitem();
                item.setPropertylist(proplistitem);
                item.setKeyname(e.getKey());
                item.setValue(e.getValue());
                items.add(item);
                //System.out.println("key="+e.getKey()+" value="+e.getValue());
            }
            proplistitem.setPropertylistitems(items);
            allprops.add(proplistitem);
        }
       this.trollingobjectDAO.setPropertylists(allprops);
    }        
}
