package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 18:33
 */
public class OracleExecutingJobQueue extends AbstractOracleJobQueue implements ExecutingJobQueue {
    public OracleExecutingJobQueue(Config config) {
        super(config);
        // create table
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_executing_job_queue.sql", getTableName()));
        }
    }

    @Override
    public boolean add(JobPo jobPo) {
        return super.add(getTableName(), jobPo);
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
    public List<JobPo> getJobs(String taskTrackerIdentity) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("TASK_TRACKER_IDENTITY = ?", taskTrackerIdentity)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    @Override
    public List<JobPo> getDeadJobs(long deadline) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("GMT_MODIFIED < ?", deadline)
                .list(RshHolder.JOB_PO_LIST_RSH);
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
    protected String getTableName(JobQueueReq request) {
        return getTableName();
    }

    private String getTableName() {
        return JobQueueUtils.EXECUTING_JOB_QUEUE.toUpperCase();
    }
}
