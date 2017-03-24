CREATE DATABASE IF NOT EXISTS knowroaming_eidelman;
USE knowroaming_eidelman;
DROP TABLE IF EXISTS usage_records;
DROP TABLE IF EXISTS data_types;
DROP TABLE IF EXISTS user_records;



CREATE TABLE user_records (
       unique_id VARCHAR(20) NOT NULL PRIMARY KEY,
       name VARCHAR(20),
       email VARCHAR(50) UNIQUE,
       phone_number VARCHAR(20));

       
CREATE TABLE data_types (
       ID int NOT NULL PRIMARY KEY AUTO_INCREMENT,
       tp_name VARCHAR(20)
);


CREATE TABLE usage_records (
       ID int NOT NULL PRIMARY KEY AUTO_INCREMENT,
       user_ID VARCHAR(20) NOT NULL,
       tp_ID int NOT NULL,
 	   time_stamp date,
       FOREIGN KEY (user_ID) REFERENCES user_records(unique_id),
       FOREIGN KEY (tp_ID) REFERENCES data_types(ID)
       );
       

INSERT INTO data_types VALUES (DEFAULT, "ALL");
INSERT INTO data_types VALUES (DEFAULT, "SMS");
INSERT INTO data_types VALUES (DEFAULT, "VOICE");
INSERT INTO data_types VALUES (DEFAULT, "DATA");

   