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

import fi.capeismi.fish.uistelupaivakirja.web.controller.LoginService;
import fi.capeismi.fish.uistelupaivakirja.web.dao.DAOStore;
import fi.capeismi.fish.uistelupaivakirja.web.dao.User;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class PublicModel {
    private DAOStore _daoStore = null;
    
    public PublicModel() {
        this._daoStore = new DAOStore(null);
    }
    
    public void setUser(User user) {
        String hash = LoginService.getMD5Hash(user.getPassword());
        user.setPassword(hash);
        this._daoStore.addUser(user);
    }
    
    public TableView getView(String viewname) {
        SearchObject search = this._daoStore.searchObject(viewname);
        return search.doSearch();
    }
}
