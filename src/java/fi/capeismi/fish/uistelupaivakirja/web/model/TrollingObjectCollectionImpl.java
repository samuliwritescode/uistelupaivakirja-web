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
import fi.capeismi.fish.uistelupaivakirja.web.dao.Trollingobject;
import fi.capeismi.fish.uistelupaivakirja.web.dao.Type;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel.EType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
class TrollingObjectCollectionImpl implements TrollingObjectCollection, DAOWrapper<Collection>{
    
    private Collection m_collectionDAO = null;
    
    @Override
    public void setDAO(Collection dao) {
        this.m_collectionDAO = dao;
    }
    
    @Override
    public Collection getDAO() {
        return m_collectionDAO;
    }
    
    public TrollingObjectCollectionImpl() {       
        m_collectionDAO = new Collection();
    }
   
    @Override
    public int getRevision() {
        return m_collectionDAO.getRevision();
    }

    @Override
    public void setRevision(int revision) {
        m_collectionDAO.setRevision(revision);
    }
     
    @Override
    public List<TrollingObject> getObjects() {
        List objects = new ArrayList<TrollingObject>();
        for(Trollingobject o: m_collectionDAO.getTrollingobjects()) {
            TrollingObjectImpl tobject = new TrollingObjectImpl();
            tobject.setDAO(o);
            objects.add(tobject);
        }
    
        return java.util.Collections.unmodifiableList(objects);
    }

    @Override
    public void addTrollingObject(TrollingObject object) {
        TrollingObjectImpl impl = (TrollingObjectImpl)object;
        impl.getDAO().setCollection(m_collectionDAO);
        this.m_collectionDAO.getTrollingobjects().add(impl.getDAO());
    }

    @Override
    public void setType(EType eType) {
        Type type = DAOStore.getType(eType.name());
        this.m_collectionDAO.setType(type);
    }
    
    @Override
    public EType getType() {
        Type type = this.m_collectionDAO.getType();
        if(type == null)
        {
            return EType.unknown;
        }
        
        if(type.getName().equalsIgnoreCase("trip"))
        {
            return EType.trip;
        }
        else if(type.getName().equalsIgnoreCase("place"))
        {
            return EType.place;
        }
        else if(type.getName().equalsIgnoreCase("lure"))
        {
            return EType.lure;
        }
        
        return EType.unknown;
    }
   
            
}
