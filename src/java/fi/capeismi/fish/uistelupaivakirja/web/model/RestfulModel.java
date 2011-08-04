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

import fi.capeismi.fish.uistelupaivakirja.web.dao.Collection;
import fi.capeismi.fish.uistelupaivakirja.web.dao.DAOStore;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class RestfulModel {
    private String m_user = null;
    private DAOStore _daoStore = null;      
    
    public RestfulModel(String user)
    {
        this.m_user = user;
        this._daoStore = DAOStore.instance(user);
    }

    
    public Object getTrollingObjects(String type)
    {        
        Object dao = this._daoStore.getCollection(type);
        if(dao instanceof Collection) {
            
            TrollingObjectCollectionImpl retval = new TrollingObjectCollectionImpl();
            if(dao != null) {
                retval.setDAO((Collection)dao);
            }
            return retval;
        }else {
            return dao;
        }

    }


    public Integer appendTrollingObjects(TrollingObjectCollection objects)
    {              
        DAOWrapper<Collection> dao = (DAOWrapper<Collection>)objects;
        this._daoStore.appendCollection(dao.getDAO());
        return new Integer(dao.getDAO().getRevision());
    }
    
    public Integer setTrollingObjects(TrollingObjectCollection objects)
    {                
        
        DAOWrapper<Collection> dao = (DAOWrapper<Collection>)objects;
        this._daoStore.setCollection(dao.getDAO());
        return new Integer(dao.getDAO().getRevision());
    }           
}
