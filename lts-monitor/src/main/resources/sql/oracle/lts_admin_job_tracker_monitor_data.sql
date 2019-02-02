create table LTS_ADMIN_JOB_TRACKER_MONITOR_DATA
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
identity VARCHAR2(64),
receive_job_num NUMBER(20),
push_job_num NUMBER(20),
exe_success_num NUMBER(20),
exe_failed_num NUMBER(20),
exe_later_num NUMBER(20),
exe_exception_num NUMBER(20),
fix_executing_job_num NUMBER(20),
timestamp NUMBER(20),
constraint PK_JOB_TRACKER_MONITOR primary key (ID)
);
create index IDX1_JOB_TRACKER_MONITOR on LTS_ADMIN_JOB_TRACKER_MONITOR_DATA (timestamp);
create index IDX2_JOB_TRACKER_MONITOR on LTS_ADMIN_JOB_TRACKER_MONITOR_DATA (identity);