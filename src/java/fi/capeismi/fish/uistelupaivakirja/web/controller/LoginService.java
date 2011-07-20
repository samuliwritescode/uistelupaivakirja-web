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
package fi.capeismi.fish.uistelupaivakirja.web.controller;

import fi.capeismi.fish.uistelupaivakirja.web.dao.TrollingHibernateUtil;
import fi.capeismi.fish.uistelupaivakirja.web.dao.User;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulException;
import fi.capeismi.fish.uistelupaivakirja.web.model.RestfulModel;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
public class LoginService {
    private Map<String, RestfulModel> m_models = new HashMap<String, RestfulModel>();
    
    public void login(String username, String password)
    {
        Session session = TrollingHibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = null;
        try
        {
            t = session.beginTransaction();
            Query userquery = session.createQuery("from User u where u.username = :usr");
            userquery.setString("usr", username);
            
            List users = userquery.list();
            System.out.println("size: "+users.size());
            
            if(users.size() == 0)
            {
                throw new RestfulException("no such user");
            }
            
            for(User var: (List<User>)users)
            {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                messageDigest.update(password.getBytes(),0, password.length());  
                String hashedPass = new BigInteger(1,messageDigest.digest()).toString(16);  
                System.out.println("User from db is "+var.getPassword());
                System.out.println("User from input is "+hashedPass);

                if(!var.getPassword().equalsIgnoreCase(hashedPass))
                {
                    throw new RestfulException("incorrect password");
                }

                getSession().setAttribute("username", var.getUsername());
            }
            
            t.commit();
        } catch(Exception e) {
            t.rollback();
            logout();
            throw new RestfulException(e.toString());
        }
    }
    
    public void logout()
    {
        getSession().removeAttribute("username");
    }
    
    private HttpSession getSession()
    {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }
    
    public RestfulModel getModel()
    {
        String user = getUserName();
        if(user == null)
        {
            throw new RestfulException("not logged in");
        }

        if(!m_models.containsKey(user))
        {
           m_models.put(user, new RestfulModel(user)); 
        }
                        
        return m_models.get(user);
    }
    
    private String getUserName()
    {
        String username = (String)getSession().getAttribute("username");
        return username;
    }
    
}
