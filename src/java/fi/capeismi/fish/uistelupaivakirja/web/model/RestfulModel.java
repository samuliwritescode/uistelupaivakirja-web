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
    
    public enum EType {unknown, trip, place, lure, spinneritem};    
    
    public RestfulModel(String user)
    {
        this.m_user = user;
        this._daoStore = DAOStore.instance(user);
    }

    
    public TrollingObjectCollection getTrollingObjects(EType type)
    {        
        Collection dao = this._daoStore.getCollection(type.name());
        TrollingObjectCollectionImpl retval = new TrollingObjectCollectionImpl();
        if(dao != null) {
            retval.setDAO(dao);
        }
        
        return retval;
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
    
    public static EType parseType(String type) {
        try {
            return RestfulModel.EType.valueOf(type);
        } catch (Exception e) {
            return RestfulModel.EType.unknown;
        }
    }
        
}
