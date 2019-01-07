package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.RepeatJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.UpdateSql;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 17:18
 */
public class OracleRepeatJobQueue extends OracleSchedulerJobQueue implements RepeatJobQueue {
    public OracleRepeatJobQueue(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_repeat_job_queue.sql", getTableName()));
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
    public int incRepeatedCount(String jobId) {
        while (true) {
            JobPo jobPo = getJob(jobId);
            if (jobPo == null) {
                return -1;
            }
            if (new UpdateSql(getSqlTemplate())
                    .update()
                    .oracleTable(getTableName())
                    .set("REPEATED_COUNT", jobPo.getRepeatedCount() + 1)
                    .where("JOB_ID = ?", jobId)
                    .and("REPEATED_COUNT = ?", jobPo.getRepeatedCount())
                    .doUpdate() == 1) {
                return jobPo.getRepeatedCount() + 1;
            }
        }
    }

    @Override
    protected String getTableName(JobQueueReq request) {
        return getTableName();
    }

    @Override
    protected String getTableName() {
        return JobQueueUtils.REPEAT_JOB_QUEUE.toUpperCase();
    }
}
