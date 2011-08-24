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

import fi.capeismi.fish.uistelupaivakirja.web.model.FillableAnnotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Samuli Penttilä <samuli.penttila@gmail.com>
 */
@XmlRootElement(name="fishlist")
public class FishStat implements FillableAnnotation {
    private List<Fish> _items = new ArrayList<Fish>();
    
    public FishStat() {
        super();
    }

    @Override
    public void add(Map<String, String> row) {                
        _items.add(new Fish(row));
    }
    
    @XmlElement
    public List<Fish> getFish() {
        return Collections.unmodifiableList(this._items);
    }

    @XmlRootElement
    public static class Fish {
        
        private Map<String, String> _values;
        
        public Fish() {
            
        }
        
        public Fish(Map<String, String> values) {
            this._values = values;
        }
        
        @XmlElement
        public String getPlace() {
            return this._values.get("place_name");
        }
        
        @XmlElement
        public String getWeight() {
            return this._values.get("fish_weight");
        }
        
        @XmlElement
        public String getLength() {
            return this._values.get("fish_length");
        }
        
        @XmlElement
        public String getLuremaker() {
            return this._values.get("lure_maker");
        }        
        
        @XmlElement
        public Date getTime() {
            try {
                String parse = this._values.get("date") +" "+this._values.get("fish_time");
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(parse);
            }catch(Exception e){
                return null;
            }
        }
    }
    
}
