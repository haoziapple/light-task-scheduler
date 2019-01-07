package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.queue.JobFeedbackQueue;
import com.github.ltsopensource.queue.domain.JobFeedbackPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-19 15:19
 */
public class OracleJobFeedbackQueue extends JdbcAbstractAccess implements JobFeedbackQueue {
    public OracleJobFeedbackQueue(Config config) {
        super(config);
    }

    @Override
    public boolean createQueue(String jobClientNodeGroup) {
        if(!isOracleTableExist(getTableName(jobClientNodeGroup))) {
            createTable(readSqlFile("sql/oracle/lts_job_feedback_queue.sql", getTableName(jobClientNodeGroup)));
        }
        return true;
    }

    @Override
    public boolean removeQueue(String jobClientNodeGroup) {
        if (isOracleTableExist(getTableName(jobClientNodeGroup))) {
            return true;
        }

        boolean removeTable = new DropTableSql(getSqlTemplate())
                .oracleDrop(getTableName(jobClientNodeGroup))
                .doDrop();

        boolean removeSeq = new DropTableSql(getSqlTemplate())
                .oracleDropSeq(getTableName(jobClientNodeGroup))
                .doDrop();

        return removeTable && removeSeq;
    }

    @Override
    public boolean add(List<JobFeedbackPo> jobFeedbackPos) {
        if (CollectionUtils.isEmpty(jobFeedbackPos)) {
            return true;
        }
        // insert ignore duplicate record
        for (JobFeedbackPo jobFeedbackPo : jobFeedbackPos) {
            String jobClientNodeGroup = jobFeedbackPo.getJobRunResult().getJobMeta().getJob().getSubmitNodeGroup();
            new InsertSql(getSqlTemplate())
                    .oracleInsert(getTableName(jobClientNodeGroup))
                    .columns("GMT_CREATED", "JOB_RESULT")
                    .values(jobFeedbackPo.getGmtCreated(), JSON.toJSONString(jobFeedbackPo.getJobRunResult()))
                    .doInsert();
        }
        return true;
    }

    @Override
    public boolean remove(String jobClientNodeGroup, String id) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName(jobClientNodeGroup))
                .where("ID = ?", id)
                .doDelete() == 1;
    }

    @Override
    public long getCount(String jobClientNodeGroup) {
        return ((BigDecimal) new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName(jobClientNodeGroup))
                .single()).longValue();
    }

    @Override
    public List<JobFeedbackPo> fetchTop(String jobClientNodeGroup, int top) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .all()
                .from()
                .oracleTable(getTableName(jobClientNodeGroup))
                .orderBy()
                .column("GMT_CREATED", OrderByType.ASC)
                .rowNumEnd(0, top)
                .list(RshHolder.JOB_FEED_BACK_LIST_RSH);
    }

    private String getTableName(String jobClientNodeGroup) {
        return JobQueueUtils.getFeedbackQueueName(jobClientNodeGroup).toUpperCase();
    }
}
