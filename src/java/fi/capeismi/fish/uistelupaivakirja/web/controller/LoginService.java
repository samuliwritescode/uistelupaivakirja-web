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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final String SALT = "SETTHISBEFOREBUILD";
    private static final int ITERATIONS = 8263;
    
    public static String generateSalt() {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[8];
            random.nextBytes(salt);
            return new BigInteger(1, salt).toString(16).toString();
        } catch (Exception ex) {
            throw new RestfulException(ex);
        }
    }
    
    public static String getMD5Hash(String from, String usersalt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.reset();
            messageDigest.update((SALT+usersalt).getBytes());
            byte[] input = messageDigest.digest(from.getBytes("UTF-8"));
            for(int loop=0; loop < ITERATIONS; loop++) {
                messageDigest.reset();
                input = messageDigest.digest(input);
            }
            return new BigInteger(1, input).toString(16).toString();
        } catch (Exception ex) {
            throw new RestfulException(ex);
        }         
    }
    
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
            
            if(users.size() == 0)
            {
                throw new RestfulException("no such user");
            }
            
            for(User var: (List<User>)users)
            {
                String hashedPass = getMD5Hash(password, var.getSalt());
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
