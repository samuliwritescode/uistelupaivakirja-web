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

import fi.capeismi.fish.uistelupaivakirja.web.dao.Revision;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class TrollingObjectCollection {
    private List<TrollingObject> m_collection = new ArrayList<TrollingObject>();
    private Revision revision = new Revision();

    public Revision getRevision() {
        return this.revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }
   
    
    public void addTrollingObject(TrollingObject o) {
        o.getDAO().setRevision(this.revision);
        this.m_collection.add(o);
    }
            
    public Collection<TrollingObject> getObjects() {
        return Collections.unmodifiableList(m_collection);        
    }
}
