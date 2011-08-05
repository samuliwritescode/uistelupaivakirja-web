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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@XmlRootElement(name="gpx")
@ViewRepresenter("fishmapview")
public class WayPoints extends View {
    private List<WayPoint> _waypoints = new ArrayList<WayPoint>();
    
    public WayPoints() {
        super();
    }
    
    @XmlElement
    public List<WayPoint> getWpt() {
        return Collections.unmodifiableList(this._waypoints);
    }

    @Override
    void add(Map<String, String> row) {
        this._waypoints.add(new WayPoint(
                row.get("fish_coord_lat"),
                row.get("fish_coord_lon"),
                row.get("date"),
                row.get("fish_time"),                
                row.get("fish_species")
                ));
    }

    @XmlRootElement
    public static class WayPoint {
        
        private String _lat, _lon, _date, _time, _name;
        
        public WayPoint() {
            
        }
        
        public WayPoint(String lat, String lon, String date, String time, String name) {
            this._lat = lat;
            this._lon = lon;
            this._date = date;
            this._time = time;
            this._name = name;
        }
        
        @XmlAttribute
        public String getLat() {
            return this._lat;
        }
        
        @XmlAttribute
        public String getLon() {
            return this._lon;
        }
        
        @XmlElement
        public Date getTime() {
            try {
                String parse = this._date+" "+this._time;
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parse);
            }catch(Exception e){
                return null;
            }
        }
        
        @XmlElement
        public String getName() {
            return this._name+" "+this._time;
        }
    }
}
