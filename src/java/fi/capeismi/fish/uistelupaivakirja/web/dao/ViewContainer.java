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

import fi.capeismi.fish.uistelupaivakirja.web.model.TableView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class ViewContainer implements TableView {
    private List<Map<String, String>> items;
    private String name;
    
    public ViewContainer(String name) {
        this.items = new ArrayList<Map<String, String>>();
        this.name = name;
    }

    public void add(Map<String, String> row) {
        this.items.add(row);
    }

    @Override
    public int rowCount() {
        return this.items.size();
    }

    @Override
    public Map<String, String> row(int index) {
        return this.items.get(index);
    }

    @Override
    public String getName() {
        return this.name;
    }
}

