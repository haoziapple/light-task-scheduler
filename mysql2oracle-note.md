# mysql转换为oracle开发笔记

- oracle中所有表名，表字段替换为大写
- oracle查询语句、更新语句中所有表字段的反引号需去除（使用SelectSql，UpdateSql中的新增方法）
- oracle分页查询使用rownum替换limit语句（使用SelectSql中的新增方法）
- "level"在oracle为关键字，所有表的"level"字段统一替换为"job_level"
- Oracle实现自增主键比较复杂，使用SnowFlakeWorker类在代码层面生成ID
