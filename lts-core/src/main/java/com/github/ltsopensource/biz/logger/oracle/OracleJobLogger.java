package com.github.ltsopensource.biz.logger.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.biz.logger.domain.JobLoggerRequest;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;
import com.github.ltsopensource.store.jdbc.builder.OrderByType;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;
import com.github.ltsopensource.store.jdbc.dbutils.JdbcTypeUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-05 19:33
 */
public class OracleJobLogger extends JdbcAbstractAccess implements JobLogger {
    public OracleJobLogger(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_job_log_po.sql"));
        }
    }

    @Override
    public void log(JobLogPo jobLogPo) {
        if (jobLogPo == null) {
            return;
        }
        InsertSql insertSql = buildInsertSql();

        setInsertSqlValues(insertSql, jobLogPo).doInsert();
    }

    @Override
    public void log(List<JobLogPo> jobLogPos) {
        if (CollectionUtils.isEmpty(jobLogPos)) {
            return;
        }

        InsertSql insertSql = buildInsertSql();

        for (JobLogPo jobLogPo : jobLogPos) {
            setInsertSqlValues(insertSql, jobLogPo);
        }
        insertSql.doBatchInsert();
    }

    private InsertSql buildInsertSql() {
        return new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName())
                .oracleColumns(
                        "ID",
                        "LOG_TIME",
                        "GMT_CREATED",
                        "LOG_TYPE",
                        "SUCCESS",
                        "MSG",
                        "TASK_TRACKER_IDENTITY",
                        "JOB_LEVEL",
                        "TASK_ID",
                        "REAL_TASK_ID",
                        "JOB_ID",
                        "JOB_TYPE",
                        "PRIORITY",
                        "SUBMIT_NODE_GROUP",
                        "TASK_TRACKER_NODE_GROUP",
                        "EXT_PARAMS",
                        "INTERNAL_EXT_PARAMS",
                        "NEED_FEEDBACK",
                        "CRON_EXPRESSION",
                        "TRIGGER_TIME",
                        "RETRY_TIMES",
                        "MAX_RETRY_TIMES",
                        "RELY_ON_PREV_CYCLE",
                        "REPEAT_COUNT",
                        "REPEATED_COUNT",
                        "REPEAT_INTERVAL"
                );
    }

    private InsertSql setInsertSqlValues(InsertSql insertSql, JobLogPo jobLogPo) {
        return insertSql.values(
                SnowFlakeWorker.getId(),
                jobLogPo.getLogTime(),
                jobLogPo.getGmtCreated(),
                jobLogPo.getLogType().name(),
                jobLogPo.isSuccess(),
                jobLogPo.getMsg(),
                jobLogPo.getTaskTrackerIdentity(),
                jobLogPo.getLevel().name(),
                jobLogPo.getTaskId(),
                jobLogPo.getRealTaskId(),
                jobLogPo.getJobId(),
                jobLogPo.getJobType() == null ? null : jobLogPo.getJobType().name(),
                jobLogPo.getPriority(),
                jobLogPo.getSubmitNodeGroup(),
                jobLogPo.getTaskTrackerNodeGroup(),
                JSON.toJSONString(jobLogPo.getExtParams()),
                JSON.toJSONString(jobLogPo.getInternalExtParams()),
                jobLogPo.isNeedFeedback(),
                jobLogPo.getCronExpression(),
                jobLogPo.getTriggerTime(),
                jobLogPo.getRetryTimes(),
                jobLogPo.getMaxRetryTimes(),
                jobLogPo.getDepPreCycle(),
                jobLogPo.getRepeatCount(),
                jobLogPo.getRepeatedCount(),
                jobLogPo.getRepeatInterval());
    }

    @Override
    public PaginationRsp<JobLogPo> search(JobLoggerRequest request) {
        PaginationRsp<JobLogPo> response = new PaginationRsp<JobLogPo>();

        Long results = ((BigDecimal) new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName())
                .whereSql(buildWhereSql(request))
                .single()).longValue();
        response.setResults(results.intValue());
        if (results == 0) {
            return response;
        }
        // 查询 rows
        List<JobLogPo> rows = new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("LOG_TIME AS log_time",
                        "GMT_CREATED AS gmt_created",
                        "LOG_TYPE AS log_type",
                        "SUCCESS AS success",
                        "MSG AS msg",
                        "TASK_TRACKER_IDENTITY AS task_tracker_identity",
                        "JOB_LEVEL AS job_level",
                        "TASK_ID AS task_id",
                        "REAL_TASK_ID AS real_task_id",
                        "JOB_TYPE AS job_type",
                        "JOB_ID AS job_id",
                        "PRIORITY AS priority",
                        "SUBMIT_NODE_GROUP AS submit_node_group",
                        "TASK_TRACKER_NODE_GROUP AS task_tracker_node_group",
                        "EXT_PARAMS AS ext_params",
                        "INTERNAL_EXT_PARAMS AS internal_ext_params",
                        "NEED_FEEDBACK AS need_feedback",
                        "CRON_EXPRESSION AS cron_expression",
                        "TRIGGER_TIME AS trigger_time",
                        "RETRY_TIMES AS retry_times",
                        "MAX_RETRY_TIMES AS max_retry_times",
                        "RELY_ON_PREV_CYCLE AS rely_on_prev_cycle",
                        "REPEAT_COUNT AS repeat_count",
                        "REPEATED_COUNT AS repeated_count",
                        "REPEAT_INTERVAL AS repeat_interval"
                        )
                .from()
                .oracleTable(getTableName())
                .whereSql(buildWhereSql(request))
                .orderBy()
                .column("LOG_TIME", OrderByType.DESC)
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHolder.ORACLE_JOB_LOGGER_LIST_RSH);
        response.setRows(rows);

        return response;
    }

    private WhereSql buildWhereSql(JobLoggerRequest request) {
        return new WhereSql()
                .andOnNotEmpty("TASK_ID = ?", request.getTaskId())
                .andOnNotEmpty("REAL_TASK_ID = ?", request.getRealTaskId())
                .andOnNotEmpty("TASK_TRACKER_NODE_GROUP = ?", request.getTaskTrackerNodeGroup())
                .andOnNotEmpty("LOG_TYPE = ?", request.getLogType())
                .andOnNotEmpty("JOB_LEVEL = ?", request.getLevel())
                .andOnNotEmpty("SUCCESS = ?", request.getSuccess())
                .andBetween("LOG_TIME", JdbcTypeUtils.toTimestamp(request.getStartLogTime()), JdbcTypeUtils.toTimestamp(request.getEndLogTime()))
                ;
    }

    private String getTableName() {
        return "lts_job_log_po".toUpperCase();
    }
}
