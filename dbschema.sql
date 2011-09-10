DROP DATABASE IF EXISTS trolling;
CREATE DATABASE trolling;
USE trolling;

CREATE TABLE user (
ID INTEGER not null auto_increment primary key,
USERNAME VARCHAR(255),
PASSWORD VARCHAR(255),
SALT VARCHAR(255),
PUBLISHLOCATION BOOLEAN,
PUBLISHPLACE BOOLEAN,
PUBLISHLURE BOOLEAN,
PUBLISHFISH BOOLEAN,
PUBLISHTRIP BOOLEAN
)ENGINE=InnoDB;

CREATE TABLE type (
ID INTEGER not null auto_increment primary key,
NAME VARCHAR(64)
) ENGINE=InnoDB;

CREATE TABLE collection (
ID INTEGER not null auto_increment primary key,
TYPE_ID INTEGER not null,
REVISION INTEGER not null,
USER_ID INTEGER not null,
    FOREIGN KEY(USER_ID) REFERENCES user(ID),
    FOREIGN KEY(TYPE_ID) REFERENCES type(ID)
)ENGINE=InnoDB;

CREATE TABLE trollingobject (
ID INTEGER not null auto_increment primary key,
COLLECTION_ID INTEGER NOT NULL,
OBJECT_IDENTIFIER INTEGER NOT NULL,
city VARCHAR(255),        
color VARCHAR(255),         
color_back VARCHAR(255),    
color_belly  VARCHAR(255),  
color_class VARCHAR(255),   
color_contrast VARCHAR(255),
color_side VARCHAR(255),    
date VARCHAR(255),          
description VARCHAR(255),   
favorite  VARCHAR(255),     
lure_type VARCHAR(255),     
maker  VARCHAR(255),        
model VARCHAR(255),         
name  VARCHAR(255),         
nickname VARCHAR(255),      
notvisible  VARCHAR(255),   
place VARCHAR(255),         
routefile VARCHAR(255),     
size  VARCHAR(255),         
time_end VARCHAR(255),       
time_start VARCHAR(255),    
waypointfile VARCHAR(255),  
    FOREIGN KEY(COLLECTION_ID) REFERENCES collection(ID)
);

CREATE TABLE event (
ID INTEGER not null auto_increment primary key,
fish_air_temp VARCHAR(255),
fish_coord_lat  VARCHAR(255),
fish_coord_lon VARCHAR(255),
fish_cr VARCHAR(255),
fish_getter VARCHAR(255),
fish_group  VARCHAR(255),
fish_group_amount VARCHAR(255),
fish_length  VARCHAR(255),
fish_line_weight VARCHAR(255),
fish_method  VARCHAR(255),
fish_misc_text VARCHAR(255),
fish_pressure VARCHAR(255),
fish_pressure_change VARCHAR(255),
fish_rain VARCHAR(255),
fish_release_width  VARCHAR(255),
fish_species  VARCHAR(255),
fish_spot_depth VARCHAR(255),
fish_time  VARCHAR(255),
fish_total_depth VARCHAR(255), 
fish_trolling_speed  VARCHAR(255),
fish_undersize   VARCHAR(255),
fish_water_temp  VARCHAR(255),
fish_weather VARCHAR(255),
fish_weight  VARCHAR(255),
fish_wind   VARCHAR(255),
fish_wind_direction  VARCHAR(255),
lure  VARCHAR(255),
type  VARCHAR(255),
TROLLING_ID INTEGER not null,
    FOREIGN KEY(TROLLING_ID) REFERENCES trollingobject(ID) 
);


INSERT INTO user(USERNAME, PASSWORD) VALUES(
'cape',
MD5('pwcape')
);

INSERT INTO type (NAME) VALUES('trip');
INSERT INTO type (NAME) VALUES('place');
INSERT INTO type (NAME) VALUES('lure');

create index trollingobject_idx on trollingobject(object_identifier);