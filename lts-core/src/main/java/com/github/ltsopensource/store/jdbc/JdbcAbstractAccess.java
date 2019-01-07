package com.github.ltsopensource.store.jdbc;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.file.FileUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.Constants;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.exception.LtsRuntimeException;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.exception.JdbcException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * @author Robert HG (254963746@qq.com) on 5/19/15.
 */
public abstract class JdbcAbstractAccess {

    private SqlTemplate sqlTemplate;
    private Config config;

    public JdbcAbstractAccess(Config config) {
        this.config = config;
        this.sqlTemplate = SqlTemplateFactory.create(config);
    }

    public SqlTemplate getSqlTemplate() {
        return sqlTemplate;
    }

    protected String readSqlFile(String path) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
        try {
            return FileUtils.read(is, Constants.CHARSET);
        } catch (IOException e) {
            throw new LtsRuntimeException("Read sql file : [" + path + "] error ", e);
        }
    }

    protected String readSqlFile(String path, String tableName) {
        String sql = readSqlFile(path);
        return sql.replace("{tableName}", tableName);
    }

    protected void createTable(String sql) throws JdbcException {
        if (config.getParameter(ExtConfig.NEED_CREATE_DB_TABLE, true)) {
            try {
                for (String s : sql.split(";")) {
                    if (StringUtils.isNotEmpty(s)) {
                        getSqlTemplate().createTable(s);
                    }
                }
            } catch (Exception e) {
                throw new JdbcException("Create table error, sql=" + sql, e);
            }
        }
    }

    protected boolean isOracleTableExist(String tableName) throws JdbcException {
        BigDecimal count = (BigDecimal) new SelectSql(getSqlTemplate())
                .select()
                .columns("COUNT(1)")
                .from()
                .oracleTable("USER_TABLES")
                .where("TABLE_NAME = ?", tableName)
                .single();

        return count.compareTo(BigDecimal.ZERO) > 0;
    }
}
