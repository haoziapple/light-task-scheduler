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

CREATE TABLE IF NOT EXISTS `lts_admin_jvm_thread` (
            `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
            `gmt_created` bigint(20) NULL DEFAULT NULL,
            `identity` varchar(64) DEFAULT NULL,
            `timestamp` bigint(20) NULL DEFAULT NULL,
            `node_type` varchar(32) NULL DEFAULT NULL,
            `node_group` varchar(64) NULL DEFAULT NULL,
            `daemon_thread_count` int(11) NULL DEFAULT NULL,
            `thread_count` int(11) NULL DEFAULT NULL,
            `total_started_thread_count` bigint(20) NULL DEFAULT NULL,
            `dead_locked_thread_count` int(11) NULL DEFAULT NULL,
            `process_cpu_time_rate` double  DEFAULT NULL,
            PRIMARY KEY (`id`),
            KEY `idx_identity` (`identity`),
            KEY `idx_timestamp` (`timestamp`)
            );