/*DROP TABLE IF EXISTS ulab_edu.person;
DROP TABLE IF EXISTS ulab_edu.book;
DROP SCHEMA IF EXISTS ulab_edu;
CREATE SCHEMA ulab_edu;

CREATE TABLE IF NOT EXISTS ulab_edu.person
(
    ID BIGSERIAL NOT NULL PRIMARY KEY,
    FULL_NAME VARCHAR (255),
    TITLE VARCHAR (255),
    AGE INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS ulab_edu.book
(
    ID BIGSERIAL NOT NULL PRIMARY KEY,
    TITLE VARCHAR (255),
    AUTHOR VARCHAR (255),
    PAGE_COUNT INTEGER NOT NULL ,
    USER_ID BIGINT
);
*/