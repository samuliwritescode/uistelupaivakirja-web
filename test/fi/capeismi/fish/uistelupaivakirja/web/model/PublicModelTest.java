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

import fi.capeismi.fish.uistelupaivakirja.web.dao.User;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class PublicModelTest {
    
    @Test
    public void testRegisterUser() {
       String username = "kekkuli";
       PublicModel model = new PublicModel();       
       User user = new User();
       user.setUsername(username);
       user.setPlaintextpassword("passu");
       model.setUser(user);
       
       RestfulModel rmodel = new RestfulModel(username);
       User dbuser = rmodel.getUser();
       assertEquals(dbuser.getUsername(), user.getUsername());
       assertFalse(dbuser.getPassword().equalsIgnoreCase("passu"));
       assertNull(dbuser.getPlaintextpassword());
    }
    
    @Test
    public void testDuplicateUser() {
       String username = "kekkuli";
       PublicModel model = new PublicModel();       
       User user = new User();
       user.setUsername(username);
       user.setPlaintextpassword("passu");
       try {
           model.setUser(user);
           fail("registration should not succeed");
       } catch(RestfulException e) {
           assertEquals(e.toString(), "user already exists");
       }
    }
}
