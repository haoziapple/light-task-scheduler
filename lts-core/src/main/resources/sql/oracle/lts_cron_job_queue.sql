create table {tableName}
(
  id  NUMBER(20) not null,
  job_id VARCHAR2(32),
  job_type  VARCHAR(32),
  priority  NUMBER(11),
  retry_times NUMBER(20) DEFAULT 0,
  max_retry_times NUMBER(11) DEFAULT 0,
  rely_on_prev_cycle NUMBER(4),
  task_id VARCHAR2(64),
  real_task_id  VARCHAR2(64),
  gmt_created NUMBER(20),
  gmt_modified NUMBER(20),
  submit_node_group VARCHAR2(64),
  task_tracker_node_group VARCHAR2(64),
  ext_params VARCHAR2(2000),
  internal_ext_params VARCHAR2(2000),
  is_running NUMBER(1),
  task_tracker_identity VARCHAR2(64),
  need_feedback  NUMBER(4),
  cron_expression VARCHAR2(128),
  trigger_time  NUMBER(20),
  repeat_count NUMBER(11) DEFAULT 0,
  repeated_count NUMBER(11) DEFAULT 0,
  repeat_interval NUMBER(20) DEFAULT 0,
  last_generate_trigger_time NUMBER(20) DEFAULT 0,
  constraint PK_{tableName} primary key (ID),
  constraint UQE1_{tableName} unique (JOB_ID),
  constraint UQE2_{tableName} unique (TASK_ID, TASK_TRACKER_NODE_GROUP)
);

create index IDX1_{tableName} on {tableName} (REAL_TASK_ID, TASK_TRACKER_NODE_GROUP);
create index IDX2_{tableName} on {tableName} (RELY_ON_PREV_CYCLE,LAST_GENERATE_TRIGGER_TIME);

CREATE SEQUENCE SEQ_{tableName}
  START WITH 1
  INCREMENT BY 1;