package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 10:50
 */
public abstract class OracleAbstractJdbcAccess extends JdbcAbstractAccess {
    public OracleAbstractJdbcAccess(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName().toUpperCase())) {
            createTable(readSqlFile("sql/oracle/" + getTableName() + ".sql"));
        }
    }

    protected abstract String getTableName();
}
