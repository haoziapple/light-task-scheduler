# mysql转换为oracle开发笔记

- oracle中所有表名，表字段替换为大写
- oracle查询语句、更新语句中所有表字段的反引号需去除（使用SelectSql，UpdateSql中的新增方法）
- oracle分页查询使用rownum替换limit语句（使用SelectSql中的新增方法）
- "level"在oracle为关键字，所有表的"level"字段统一替换为"job_level"
- "group"是关键字，在oracle中替换为"node_group"
- Oracle实现自增主键比较复杂，使用SnowFlakeWorker类在代码层面生成ID
- Oracle对表名长度有限制，而lts将任务id作为任务队列表名，所以任务的taskGroupId需要限制20个字符
- lts_admin_job_client_monitor_data 表名过长，修改为mdata
- lts_admin_job_tracker_monitor_data 表名过长，修改为mdata
- lts_admin_task_tracker_monitor_data 表名过长，修改为mdata
- Oracle下count(1)返回的为BigDecimal类型，需要转换为long类型
- Oracle下不支持"group by TIMESTAMP ASC"的语法，需要改写为"group by TIMESTAMP order by TIMESTAMP ASC"
- oracle rs.getTimestamp("log_time")获取日期类型数据时报错：getTimestamp not implemented for class oracle.jdbc.driver.T4CNumberAccessor, 使用new Date(rs.getLong("log_time"))进行转换