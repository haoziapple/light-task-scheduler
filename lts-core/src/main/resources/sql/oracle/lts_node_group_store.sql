create table {tableName}
(
  node_type  VARCHAR2(16) not null,
  name  VARCHAR2(16) not null,
  gmt_created NUMBER(20),
  constraint PK_{tableName} primary key (node_type, name)
)