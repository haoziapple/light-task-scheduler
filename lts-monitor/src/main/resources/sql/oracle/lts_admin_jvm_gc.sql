create table LTS_ADMIN_JVM_GC
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
identity VARCHAR2(64),
timestamp NUMBER(20),
node_type VARCHAR2(32),
node_group VARCHAR2(64),
young_gc_collection_count NUMBER(20),
young_gc_collection_time NUMBER(20),
full_gc_collection_count NUMBER(20),
full_gc_collection_time NUMBER(20),
span_young_gc_collection_count NUMBER(20),
span_young_gc_collection_time NUMBER(20),
span_full_gc_collection_count NUMBER(20),
span_full_gc_collection_time NUMBER(20),
constraint PK_JVM_GC primary key (ID)
);
create index IDX1_JVM_GC on LTS_ADMIN_JVM_GC (timestamp);
create index IDX2_JVM_GC on LTS_ADMIN_JVM_GC (identity);