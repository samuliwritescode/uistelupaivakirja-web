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
package fi.capeismi.fish.uistelupaivakirja.web.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
abstract class View {
    
    public static final String viewname = "*OVERRIDETHIS*";
    private List<String> _columns = new ArrayList<String>();
    
    public final void addColumn(String colname) {
        this._columns.add(colname);
    }
    
    public final List<String> getColumns() {
        return Collections.unmodifiableList(this._columns);
    }
    
    public final void add(Object[] row) {
        Map<String, String> rowmap = new HashMap<String, String>();
        for(int loop=0; loop < this._columns.size(); loop++) {
            rowmap.put(this._columns.get(loop), (String)row[loop]);
        }
        add(rowmap);
    }
    
    abstract void add(Map<String, String> row);
    abstract String getQuery();   
}
