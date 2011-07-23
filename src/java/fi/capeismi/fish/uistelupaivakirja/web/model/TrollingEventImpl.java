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
import fi.capeismi.fish.uistelupaivakirja.web.dao.Eventproperty;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Keyvalue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
class TrollingEventImpl implements TrollingEvent, DAOWrapper<Event>{
    private Event _eventDAO;
    
    public TrollingEventImpl() {
        this._eventDAO = new Event();
    }
    
    @Override
    public void setDAO(Event dao) {
        this._eventDAO = (Event)dao;
    }
    
    @Override
    public Event getDAO() {
        return this._eventDAO;
    }

    @Override
    public void setKeyValue(String key, String value) {
        Eventproperty property = new Eventproperty();
        Keyvalue kvpair = new Keyvalue();
        kvpair.setKeyname(key);
        kvpair.setValue(value);
        property.setKeyvalue(kvpair);
        property.setEvent(_eventDAO);              
        _eventDAO.getEventproperties().add(property);
    }
    
    @Override
    public Map<String, String> getKeyvalues() {
       Map<String, String> retval = new HashMap<String, String>();
       Set<Eventproperty> keyvalues = this._eventDAO.getEventproperties();
       for(Eventproperty keyvalue: keyvalues)
       {
           String key = keyvalue.getKeyvalue().getKeyname();
           String value = keyvalue.getKeyvalue().getValue();
           retval.put(key, value);
       }

       return Collections.unmodifiableMap(retval);
    }

}
