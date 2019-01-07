package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.SchedulerJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.UpdateSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 16:49
 */
public abstract class OracleSchedulerJobQueue extends AbstractOracleJobQueue implements SchedulerJobQueue {
    public OracleSchedulerJobQueue(Config config) {
        super(config);
    }

    @Override
    public boolean updateLastGenerateTriggerTime(String jobId, Long lastGenerateTriggerTime) {
        return new UpdateSql(getSqlTemplate())
                .update()
                .oracleTable(getTableName())
                .set("LAST_GENERATE_TRIGGER_TIME", lastGenerateTriggerTime)
                .set("GMT_MODIFIED", SystemClock.now())
                .where("JOB_ID = ? ", jobId)
                .doUpdate() == 1;
    }

    @Override
    public List<JobPo> getNeedGenerateJobPos(Long checkTime, int topSize) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("RELY_ON_PREV_CYCLE = ?", false)
                .and("LAST_GENERATE_TRIGGER_TIME <= ?", checkTime)
                .rowNumEnd(0, topSize)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    protected abstract String getTableName();
}
