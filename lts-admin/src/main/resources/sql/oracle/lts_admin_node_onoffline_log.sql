create table lts_admin_node_onoffline_log
(
  id  NUMBER(20) not null,
  log_time NUMBER(20),
  event VARCHAR2(32),
  node_type VARCHAR2(16),
  cluster_name VARCHAR2(64),
  ip VARCHAR2(16),
  port NUMBER(11),
  host_name VARCHAR2(64),
  node_group VARCHAR2(64),
  create_time NUMBER(20),
  threads NUMBER(11),
  identity VARCHAR2(64),
  http_cmd_port NUMBER(11),
  constraint PK_admin_node_onoffline_log primary key (ID)
);
create index IDX1_admin_node_onoffline_log on lts_admin_node_onoffline_log (LOG_TIME);
create index IDX2_admin_node_onoffline_log on lts_admin_node_onoffline_log (EVENT);
create index IDX3_admin_node_onoffline_log on lts_admin_node_onoffline_log (IDENTITY);
create index IDX4_admin_node_onoffline_log on lts_admin_node_onoffline_log (node_group);