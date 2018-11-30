create table {tableName}
(
  id  NUMBER(20) not null,
  gmt_created NUMBER(20),
  job_result VARCHAR2(2000),
  constraint UQE1_{tableName} unique (ID)
);

create index IDX1_{tableName} on {tableName} (GMT_CREATED);

CREATE SEQUENCE SEQ_{tableName}
  START WITH 1
  INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TRIGGER_{tableName}
  BEFORE INSERT ON {tableName}
  FOR EACH ROW
BEGIN
  SELECT SEQ_{tableName}.NEXTVAL
    INTO :new.ID
    FROM dual;
END;