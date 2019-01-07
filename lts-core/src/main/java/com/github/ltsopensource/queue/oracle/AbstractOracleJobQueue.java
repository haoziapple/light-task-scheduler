package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.Assert;
import com.github.ltsopensource.core.commons.utils.CharacterUtils;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.JobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.*;
import com.github.ltsopensource.store.jdbc.dbutils.JdbcTypeUtils;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 16:31
 */
public abstract class AbstractOracleJobQueue extends JdbcAbstractAccess implements JobQueue {
    public AbstractOracleJobQueue(Config config) {
        super(config);
    }

    protected boolean add(String tableName, JobPo jobPo) {
        return new InsertSql(getSqlTemplate())
                .oracleInsert(tableName)
                .oracleColumns(
                        "ID",
                        "JOB_ID",
                        "JOB_TYPE",
                        "PRIORITY",
                        "RETRY_TIMES",
                        "MAX_RETRY_TIMES",
                        "RELY_ON_PREV_CYCLE",
                        "TASK_ID",
                        "REAL_TASK_ID",
                        "GMT_CREATED",
                        "GMT_MODIFIED",
                        "SUBMIT_NODE_GROUP",
                        "TASK_TRACKER_NODE_GROUP",
                        "EXT_PARAMS",
                        "INTERNAL_EXT_PARAMS",
                        "IS_RUNNING",
                        "TASK_TRACKER_IDENTITY",
                        "NEED_FEEDBACK",
                        "CRON_EXPRESSION",
                        "TRIGGER_TIME",
                        "REPEAT_COUNT",
                        "REPEATED_COUNT",
                        "REPEAT_INTERVAL")
                .values(
                        "SEQ_" + tableName + ".nextval",
                        jobPo.getJobId(),
                        jobPo.getJobType() == null ? null : jobPo.getJobType().name(),
                        jobPo.getPriority(),
                        jobPo.getRetryTimes(),
                        jobPo.getMaxRetryTimes(),
                        jobPo.getRelyOnPrevCycle(),
                        jobPo.getTaskId(),
                        jobPo.getRealTaskId(),
                        jobPo.getGmtCreated(),
                        jobPo.getGmtModified(),
                        jobPo.getSubmitNodeGroup(),
                        jobPo.getTaskTrackerNodeGroup(),
                        JSON.toJSONString(jobPo.getExtParams()),
                        JSON.toJSONString(jobPo.getInternalExtParams()),
                        jobPo.isRunning(),
                        jobPo.getTaskTrackerIdentity(),
                        jobPo.isNeedFeedback(),
                        jobPo.getCronExpression(),
                        jobPo.getTriggerTime(),
                        jobPo.getRepeatCount(),
                        jobPo.getRepeatedCount(),
                        jobPo.getRepeatInterval())
                .doInsert() == 1;
    }

    @Override
    public PaginationRsp<JobPo> pageSelect(JobQueueReq request) {
        PaginationRsp<JobPo> response = new PaginationRsp<JobPo>();

        WhereSql whereSql = buildWhereSql(request);

        Long results = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName(request))
                .whereSql(whereSql)
                .single();
        response.setResults(results.intValue());

        if (results > 0) {
            List<JobPo> jobPos = new SelectSql(getSqlTemplate())
                    .rowNumStart()
                    .select()
                    .all()
                    .from()
                    .oracleTable(getTableName(request))
                    .whereSql(whereSql)
                    .orderBy()
                    .column(CharacterUtils.camelCase2Underscore(request.getField()).toUpperCase(), OrderByType.convert(request.getDirection()))
                    .rowNumEnd(request.getStart(), request.getLimit())
                    .list(RshHolder.JOB_PO_LIST_RSH);
            response.setRows(jobPos);
        }
        return response;
    }

    protected abstract String getTableName(JobQueueReq request);

    @Override
    public boolean selectiveUpdateByJobId(JobQueueReq request) {
        Assert.hasLength(request.getJobId(), "Only allow update by jobId");

        UpdateSql sql = buildUpdateSqlPrefix(request);

        return sql.where("JOB_ID=?", request.getJobId())
                .doUpdate() == 1;
    }

    @Override
    public boolean selectiveUpdateByTaskId(JobQueueReq request) {
        Assert.hasLength(request.getRealTaskId(), "Only allow update by realTaskId and taskTrackerNodeGroup");
        Assert.hasLength(request.getTaskTrackerNodeGroup(), "Only allow update by realTaskId and taskTrackerNodeGroup");

        UpdateSql sql = buildUpdateSqlPrefix(request);
        return sql.where("REAL_TASK_ID = ?", request.getRealTaskId())
                .and("TASK_TRACKER_NODE_GROUP = ?", request.getTaskTrackerNodeGroup())
                .doUpdate() == 1;
    }


    private UpdateSql buildUpdateSqlPrefix(JobQueueReq request) {
        return new UpdateSql(getSqlTemplate())
                .update()
                .oracleTable(getTableName(request))
                .setOnNotNull("CRON_EXPRESSION", request.getCronExpression())
                .setOnNotNull("NEED_FEEDBACK", request.getNeedFeedback())
                .setOnNotNull("EXT_PARAMS", JSON.toJSONString(request.getExtParams()))
                .setOnNotNull("TRIGGER_TIME", JdbcTypeUtils.toTimestamp(request.getTriggerTime()))
                .setOnNotNull("PRIORITY", request.getPriority())
                .setOnNotNull("MAX_RETRY_TIMES", request.getMaxRetryTimes())
                .setOnNotNull("RELY_ON_PREV_CYCLE", request.getRelyOnPrevCycle() == null ? true : request.getRelyOnPrevCycle())
                .setOnNotNull("SUBMIT_NODE_GROUP", request.getSubmitNodeGroup())
                .setOnNotNull("TASK_TRACKER_NODE_GROUP", request.getTaskTrackerNodeGroup())
                .setOnNotNull("REPEAT_COUNT", request.getRepeatCount())
                .setOnNotNull("REPEAT_INTERVAL", request.getRepeatInterval())
                .setOnNotNull("GMT_MODIFIED", SystemClock.now());
    }

    private WhereSql buildWhereSql(JobQueueReq request) {
        return new WhereSql()
                .andOnNotEmpty("JOB_ID = ?", request.getJobId())
                .andOnNotEmpty("TASK_ID = ?", request.getTaskId())
                .andOnNotEmpty("REAL_TASK_ID = ?", request.getRealTaskId())
                .andOnNotEmpty("TASK_TRACKER_NODE_GROUP = ?", request.getTaskTrackerNodeGroup())
                .andOnNotEmpty("JOB_TYPE = ?", request.getJobType())
                .andOnNotEmpty("SUBMIT_NODE_GROUP = ?", request.getSubmitNodeGroup())
                .andOnNotNull("NEED_FEEDBACK = ?", request.getNeedFeedback())
                .andBetween("GMT_CREATED", JdbcTypeUtils.toTimestamp(request.getStartGmtCreated()), JdbcTypeUtils.toTimestamp(request.getEndGmtCreated()))
                .andBetween("GMT_MODIFIED", JdbcTypeUtils.toTimestamp(request.getStartGmtModified()), JdbcTypeUtils.toTimestamp(request.getEndGmtModified()));
    }
}
