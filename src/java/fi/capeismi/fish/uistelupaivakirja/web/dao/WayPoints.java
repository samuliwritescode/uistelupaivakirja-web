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
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@XmlRootElement(name="gpx")
public class WayPoints {
    
    @XmlElement
    public List<WayPoint> getWpt() {
        List<WayPoint> retval = new ArrayList<WayPoint>();
        
        retval.add(new WayPoint());
        retval.add(new WayPoint());
        retval.add(new WayPoint());
        retval.add(new WayPoint());
        return retval;
    }

    @XmlRootElement
    public static class WayPoint {
        
        @XmlAttribute
        public String getLat() {
            return "65.323";
        }
        
        @XmlAttribute
        public String getLon() {
            return "25.233";
        }
        
        @XmlElement
        public Date getTime() {
            return new Date();
        }
        
        @XmlElement
        public String getName() {
            return "hauki";
        }
    }
}
