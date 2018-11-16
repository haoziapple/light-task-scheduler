declare
  num number;
begin
  select count(1) into num from USER_TABLES where TABLE_NAME='LTS_JOB_LOG_PO';
  if num = 1 then
  EXECUTE IMMEDIATE '';
  END IF;
end ;
/


create table LTS_JOB_LOG_PO
(
  id  NUMBER(20) not null,
  gmt_created NUMBER(20),
  log_time  NUMBER(20),
  log_type  VARCHAR2(32),
  success NUMBER(11),
  msg VARCHAR2(2000),
  code  VARCHAR2(32),
  job_type  VARCHAR2(32),
  task_tracker_identity VARCHAR2(64),
  level VARCHAR2(32),
  task_id VARCHAR2(64),
  real_task_id  VARCHAR2(64),
  job_id  VARCHAR2(64) DEFAULT '',
  priority  NUMBER(11),
  submit_node_group VARCHAR2(64),
  task_tracker_node_group VARCHAR2(64),
  ext_params VARCHAR2(2000),
  internal_ext_params VARCHAR2(2000),
  need_feedback  NUMBER(4),
  cron_expression VARCHAR2(128),
  trigger_time  NUMBER(20),
  retry_times NUMBER(20),
  max_retry_times NUMBER(11) DEFAULT 0,
  rely_on_prev_cycle NUMBER(4),
  repeat_count NUMBER(11) DEFAULT 0,
  repeated_count NUMBER(11) DEFAULT 0,
  repeat_interval NUMBER(20) DEFAULT 0
);
alter table LTS_JOB_LOG_PO
  add constraint PK_JOB_LOG primary key (ID)
  using index;
create index IDX1_JOB_LOG on LTS_JOB_LOG_PO (log_time);
create index IDX2_JOB_LOG on LTS_JOB_LOG_PO (task_id,task_tracker_node_group);
create index IDX3_JOB_LOG on LTS_JOB_LOG_PO (real_task_id,task_tracker_node_group);

CREATE SEQUENCE SEQ_JOB_LOG
  START WITH 1
  INCREMENT BY 1;

CREATE OR REPLACE TRIGGER TRIGGER_JOB_LOG
  BEFORE INSERT ON LTS_JOB_LOG_PO
  FOR EACH ROW
BEGIN
  SELECT SEQ_JOB_LOG.NEXTVAL
    INTO :new.ID
    FROM dual;
END;
