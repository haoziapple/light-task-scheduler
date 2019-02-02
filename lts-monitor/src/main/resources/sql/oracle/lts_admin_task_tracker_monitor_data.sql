create table LTS_ADMIN_TASK_TRACKER_MONITOR_DATA
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
node_group VARCHAR2(64),
identity VARCHAR2(64),
exe_success_num NUMBER(20),
exe_failed_num NUMBER(20),
exe_later_num NUMBER(20),
exe_exception_num NUMBER(20),
total_running_time NUMBER(20),
timestamp NUMBER(20),
constraint PK_TASK_TRACKER_MONITOR primary key (ID)
);
create index IDX1_TASK_TRACKER_MONITOR on LTS_ADMIN_TASK_TRACKER_MONITOR_DATA (timestamp);
create index IDX2_TASK_TRACKER_MONITOR on LTS_ADMIN_TASK_TRACKER_MONITOR_DATA (identity);
create index IDX3_TASK_TRACKER_MONITOR on LTS_ADMIN_TASK_TRACKER_MONITOR_DATA (node_group);