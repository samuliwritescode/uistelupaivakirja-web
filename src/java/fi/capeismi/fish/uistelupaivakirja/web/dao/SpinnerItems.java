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
@XmlRootElement(name="TrollingObjects")
public class SpinnerItems {
        
    List<SpinnerItem> _items;
    
    public SpinnerItems() {
        this._items = new ArrayList<SpinnerItem>();
    }
       
    public void add(SpinnerItem item) {
        
        this._items.add(item);
    }
    
    @XmlAttribute(name="MaxId")
    public Integer getMaxId() {
        return new Integer(this._items.size());
    }
    
    @XmlElement(name="TrollingObject")
    public List<SpinnerItem> getItems() {
        return this._items;
    }
     
    public static class SpinnerItem {
        private String _key;
        private String _value;
        private int _id;
        
        public SpinnerItem() {
            
        }
        
        public SpinnerItem(String key, String value, int id) {
            this._key = key;
            this._value = value;
            this._id = id;
        }
        
        public void setId(int id) {
            this._id = id;
        }
        
        public void setKeyname(String key) {
            this._key = key;         
        }
    
        public void setValue(String value) {
            this._value = value;            
        }
        
        @XmlAttribute
        public Integer getId() {
            return new Integer(this._id);
        }
        
        @XmlAttribute(name="type")
        public String getSpinnerItemString() {
            return "spinneritem";
        }
        
        @XmlElement
        public String getValue() {
            return this._value;
        }
                
        @XmlElement
        public String getType() {
            return this._key;
        }        
    }
}
