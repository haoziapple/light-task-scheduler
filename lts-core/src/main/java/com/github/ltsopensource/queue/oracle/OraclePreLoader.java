package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.core.AppContext;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.AbstractPreLoader;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.SqlTemplate;
import com.github.ltsopensource.store.jdbc.SqlTemplateFactory;
import com.github.ltsopensource.store.jdbc.builder.OrderByType;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.UpdateSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-19 15:56
 */
public class OraclePreLoader extends AbstractPreLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(OraclePreLoader.class);
    private SqlTemplate sqlTemplate;

    public OraclePreLoader(AppContext appContext) {
        super(appContext);
        this.sqlTemplate = SqlTemplateFactory.create(appContext.getConfig());
    }

    @Override
    protected JobPo getJob(String taskTrackerNodeGroup, String jobId) {
        return new SelectSql(sqlTemplate)
                .select()
                .all()
                .from()
                .oracleTable(getTableName(taskTrackerNodeGroup))
                .where("JOB_ID = ?", jobId)
                .and("TASK_TRACKER_NODE_GROUP = ?", taskTrackerNodeGroup)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    protected boolean lockJob(String taskTrackerNodeGroup, String jobId, String taskTrackerIdentity, Long triggerTime, Long gmtModified) {
        try {
            return new UpdateSql(sqlTemplate)
                    .update()
                    .oracleTable(getTableName(taskTrackerNodeGroup))
                    .oracleSet("IS_RUNNING", true)
                    .oracleSet("TASK_TRACKER_IDENTITY", taskTrackerIdentity)
                    .oracleSet("GMT_MODIFIED", SystemClock.now())
                    .where("JOB_ID = ?", jobId)
                    .and("IS_RUNNING = ?", false)
                    .and("TRIGGER_TIME = ?", triggerTime)
                    .and("GMT_MODIFIED = ?", gmtModified)
                    .doUpdate() == 1;
        } catch (Exception e) {
            LOGGER.error("Error when lock job:" + e.getMessage(), e);
            return false;
        }
    }

    @Override
    protected List<JobPo> load(String loadTaskTrackerNodeGroup, int loadSize) {
        try {
            return new SelectSql(sqlTemplate)
                    .rowNumStart()
                    .select()
                    .all()
                    .from()
                    .oracleTable(getTableName(loadTaskTrackerNodeGroup))
                    .where("IS_RUNNING = ?", false)
                    .and("TRIGGER_TIME< ?", SystemClock.now())
                    .orderBy()
                    .column("PRIORITY", OrderByType.ASC)
                    .column("TRIGGER_TIME", OrderByType.ASC)
                    .column("GMT_CREATED", OrderByType.ASC)
                    .rowNumEnd(0, loadSize)
                    .list(RshHolder.JOB_PO_LIST_RSH);
        } catch (Exception e) {
            LOGGER.error("Error when load job:" + e.getMessage(), e);
            return null;
        }
    }

    private String getTableName(String taskTrackerNodeGroup) {
        return JobQueueUtils.getExecutableQueueName(taskTrackerNodeGroup)
                .replaceAll("-", "_")
                .toUpperCase();
    }
}
