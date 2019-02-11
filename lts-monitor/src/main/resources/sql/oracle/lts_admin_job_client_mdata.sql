create table LTS_ADMIN_JOB_CLIENT_MDATA
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
node_group VARCHAR2(64),
identity VARCHAR2(64),
submit_success_num NUMBER(20),
submit_failed_num NUMBER(20),
fail_store_num NUMBER(20),
submit_fail_store_num NUMBER(20),
handle_feedback_num NUMBER(20),
timestamp NUMBER(20),
constraint PK_JOB_CLIENT_MONITOR primary key (ID)
);

create index IDX1_JOB_CLIENT_MONITOR on LTS_ADMIN_JOB_CLIENT_MDATA (timestamp);
create index IDX2_JOB_CLIENT_MONITOR on LTS_ADMIN_JOB_CLIENT_MDATA (identity);
create index IDX3_JOB_CLIENT_MONITOR on LTS_ADMIN_JOB_CLIENT_MDATA (node_group);