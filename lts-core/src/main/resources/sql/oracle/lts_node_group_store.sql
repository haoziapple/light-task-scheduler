create table {tableName}
(
  node_type  VARCHAR2(16) not null DEFAULT '',
  name  VARCHAR2(16) not null DEFAULT '',
  gmt_created NUMBER(20),
  constraint PK_{tableName} primary key (node_type, name)
);