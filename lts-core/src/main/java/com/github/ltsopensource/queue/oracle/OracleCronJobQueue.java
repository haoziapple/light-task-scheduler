package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.CronJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 16:28
 */
public class OracleCronJobQueue extends OracleSchedulerJobQueue implements CronJobQueue {
    public OracleCronJobQueue(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_cron_job_queue.sql", getTableName()));
        }
    }



    @Override
    public boolean add(JobPo jobPo) {
        return super.add(getTableName(), jobPo);
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

    @Override
    protected String getTableName() {
        return JobQueueUtils.CRON_JOB_QUEUE.toUpperCase();
    }
}
