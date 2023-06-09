 DROP TABLE IF EXISTS SETTINGS;
 CREATE TABLE IF NOT EXISTS WORK_ITEM
  (
     ID                       BIGINT GENERATED BY DEFAULT AS IDENTITY,
     COMPLETED_DATE_TIME      TIMESTAMP,
     CREATED_DATE_TIME        TIMESTAMP,
     DUE_DATE_TIME            TIMESTAMP,

     FINISHED                 BOOLEAN NOT NULL,
     NOTE                     CLOB NOT NULL,
     PRIORITY                 VARCHAR(255) NOT NULL,
     PROJECT                  VARCHAR(255) NOT NULL,
     TODO                     CLOB NOT NULL,
     PRIMARY KEY (ID)
  );

ALTER TABLE WORK_ITEM ALTER COLUMN NOTE CLOB;
ALTER TABLE WORK_ITEM ALTER COLUMN TODO CLOB;
ALTER TABLE WORK_ITEM ALTER COLUMN PROJECT CLOB;
ALTER TABLE WORK_ITEM ALTER COLUMN TODO CLOB;