create table LTS_ADMIN_JVM_MEMORY
(
id  NUMBER(20) not null,
gmt_created NUMBER(20),
identity VARCHAR2(64),
timestamp NUMBER(20),
node_type VARCHAR2(32),
node_group VARCHAR2(64),
heap_memory_committed NUMBER(20),
heap_memory_init NUMBER(20),
heap_memory_max NUMBER(20),
heap_memory_used NUMBER(20),
non_heap_memory_committed NUMBER(20),
non_heap_memory_init NUMBER(20),
non_heap_memory_max NUMBER(20),
non_heap_memory_used NUMBER(20),
perm_gen_committed NUMBER(20),
perm_gen_init NUMBER(20),
perm_gen_max NUMBER(20),
perm_gen_used NUMBER(20),
old_gen_committed NUMBER(20),
old_gen_init NUMBER(20),
old_gen_max NUMBER(20),
old_gen_used NUMBER(20),
eden_space_committed NUMBER(20),
eden_space_init NUMBER(20),
eden_space_max NUMBER(20),
eden_space_used NUMBER(20),
survivor_committed NUMBER(20),
survivor_init NUMBER(20),
survivor_max NUMBER(20),
survivor_used NUMBER(20),
constraint PK_JVM_MEMORY primary key (ID)
);
create index IDX1_JVM_MEMORY on LTS_ADMIN_JVM_MEMORY (timestamp);
create index IDX2_JVM_MEMORY on LTS_ADMIN_JVM_MEMORY (identity);