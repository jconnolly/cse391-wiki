CREATE TABLE wiki (
id              int NOT NULL PRIMARY KEY AUTO_INCREMENT,
title           varchar(255) NOT NULL,
body            MEDIUMTEXT NOT NULL,
redirect        varchar(255) DEFAULT NULL
);