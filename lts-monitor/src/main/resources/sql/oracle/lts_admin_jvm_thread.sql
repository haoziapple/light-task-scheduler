create table LTS_ADMIN_JVM_THREAD
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
identity VARCHAR2(64),
timestamp NUMBER(20),
node_type VARCHAR2(32),
node_group VARCHAR2(64),
daemon_thread_count NUMBER(20),
thread_count NUMBER(20),
total_started_thread_count NUMBER(20),
dead_locked_thread_count NUMBER(20),
process_cpu_time_rate NUMBER(20,4),
constraint PK_JVM_THREAD primary key (ID)
);
create index IDX1_JVM_THREAD on LTS_ADMIN_JVM_THREAD (timestamp);
create index IDX2_JVM_THREAD on LTS_ADMIN_JVM_THREAD (identity);