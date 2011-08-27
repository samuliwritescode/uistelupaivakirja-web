DROP DATABASE IF EXISTS trolling;
CREATE DATABASE trolling;
USE trolling;

CREATE TABLE user (
ID INTEGER not null auto_increment primary key,
USERNAME VARCHAR(255),
PASSWORD VARCHAR(255),
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
    FOREIGN KEY(COLLECTION_ID) REFERENCES collection(ID)
);

CREATE TABLE trollingproperty (
ID INTEGER not null auto_increment primary key,
TROLLING_ID INTEGER not null,
KEYNAME VARCHAR(255) NOT NULL,
VALUE TEXT CHARACTER SET utf8,
    FOREIGN KEY(TROLLING_ID) REFERENCES trollingobject(ID)
);

CREATE TABLE event (
ID INTEGER not null auto_increment primary key,
TROLLING_ID INTEGER not null,
    FOREIGN KEY(TROLLING_ID) REFERENCES trollingobject(ID) 
);

CREATE TABLE eventproperty (
ID INTEGER not null auto_increment primary key,
EVENT_ID INTEGER not null,
KEYNAME VARCHAR(255) not null,
VALUE TEXT CHARACTER SET utf8,
    FOREIGN KEY(EVENT_ID) REFERENCES event(ID)
);

INSERT INTO user(USERNAME, PASSWORD) VALUES(
'cape',
MD5('pwcape')
);

INSERT INTO type (NAME) VALUES('trip');
INSERT INTO type (NAME) VALUES('place');
INSERT INTO type (NAME) VALUES('lure');

create index trollingobject_idx on trollingobject(object_identifier);
create index eventproperty_idx on eventproperty(keyname);
create index trollingproperty_idx on trollingproperty(keyname);