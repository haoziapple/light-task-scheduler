package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.SuspendJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-19 15:50
 */
public class OracleSuspendJobQueue extends AbstractOracleJobQueue implements SuspendJobQueue {
    public OracleSuspendJobQueue(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_suspend_job_queue.sql", getTableName()));
        }
    }

    @Override
    public boolean add(JobPo jobPo) {
        return add(getTableName(), jobPo);
    }

    @Override
    public JobPo getJob(String jobId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("JOB_ID = ?", jobId)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    public boolean remove(String jobId) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName())
                .where("JOB_ID = ?", jobId)
                .doDelete() == 1;
    }

    @Override
    public JobPo getJob(String taskTrackerNodeGroup, String taskId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("TASK_ID = ?", taskId)
                .and("TASK_TRACKER_NODE_GROUP = ?", taskTrackerNodeGroup)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    protected String getTableName(JobQueueReq request) {
        return getTableName();
    }

    private String getTableName() {
        return JobQueueUtils.SUSPEND_JOB_QUEUE.toUpperCase();
    }
}
