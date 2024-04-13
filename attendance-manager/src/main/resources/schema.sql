SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS member;

CREATE TABLE member
(
   id INT NOT NULL AUTO_INCREMENT,
   name VARCHAR(100),
   grade INT,
   PRIMARY KEY(id)
);

DROP TABLE IF EXISTS attendance_point;

CREATE TABLE attendance_point
(
   attendance VARCHAR(100) NOT NULL,
   point INT,
   PRIMARY KEY(attendance)
);

DROP TABLE IF EXISTS attendance_status;

CREATE TABLE attendance_status
(
   id INT NOT NULL,
   year INT,
   month INT,
   day INT,
   attendance VARCHAR(100),
   PRIMARY KEY(id,year,month,day),
   FOREIGN KEY (id) REFERENCES member(id),
   FOREIGN KEY (attendance) REFERENCES attendance_point(attendance)
);

SET FOREIGN_KEY_CHECKS = 1;