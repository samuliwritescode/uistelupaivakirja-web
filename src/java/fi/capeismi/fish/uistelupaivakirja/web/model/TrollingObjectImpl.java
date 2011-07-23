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

import fi.capeismi.fish.uistelupaivakirja.web.dao.Event;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Keyvalue;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Property;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
class TrollingObjectImpl implements TrollingObject, DAOWrapper<Trollingobject> {
    private Trollingobject trollingobjectDAO = null;  
   
    public TrollingObjectImpl()
    {
        this.trollingobjectDAO = new Trollingobject();
    }
    
    @Override
    public Trollingobject getDAO()
    {
        return this.trollingobjectDAO;
    }
    
    @Override
    public void setDAO(Trollingobject dao)
    {
        this.trollingobjectDAO = dao;
    }

    @Override
    public int getId() {
        return this.trollingobjectDAO.getObjectIdentifier();
    }

    @Override
    public void setId(int id) {
        this.trollingobjectDAO.setObjectIdentifier(id);
    }


    @Override
    public Map<String, String> getKeyvalues() {
       Map<String, String> retval = new HashMap<String, String>();
       for(Property property: this.trollingobjectDAO.getProperties())
       {
           retval.put(property.getKeyvalue().getKeyname(), 
                   property.getKeyvalue().getValue());
       }

       return Collections.unmodifiableMap(retval);
    }
    
    @Override
    public List<TrollingEvent> getEvents() {
        List<TrollingEvent> retval = new ArrayList();
        for(Event event: this.trollingobjectDAO.getEvents())
        {
            TrollingEventImpl tevent = new TrollingEventImpl();
            tevent.setDAO(event);
            retval.add(tevent);
        }
        return Collections.unmodifiableList(retval);
    }
    
    
    @Override
    public void setKeyValue(String key, String value)
    {
        Property property = new Property();
        Keyvalue kv = new Keyvalue();
        kv.setKeyname(key);
        kv.setValue(value);
        property.setKeyvalue(kv);
        property.setTrollingobject(trollingobjectDAO);
        this.trollingobjectDAO.getProperties().add(property);
    }
    
    

    @Override
    public void addEvent(TrollingEvent event) {
        TrollingEventImpl impl = (TrollingEventImpl)event;
        Event dao = impl.getDAO();
        dao.setTrollingobject(trollingobjectDAO);
        this.trollingobjectDAO.getEvents().add(dao);
    }
}
