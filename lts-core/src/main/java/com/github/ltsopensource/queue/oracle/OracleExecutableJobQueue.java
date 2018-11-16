package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.request.JobQueueReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.domain.JobPo;
import com.github.ltsopensource.queue.mysql.AbstractMysqlJobQueue;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.DropTableSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.UpdateSql;
import com.github.ltsopensource.store.jdbc.exception.TableNotExistException;

import java.util.List;

/**
 * @author wanghao
 * @Description 可执行队列是通过创建中间表保存的
 * @date 2018-11-15 17:26
 */
public class OracleExecutableJobQueue extends AbstractMysqlJobQueue implements ExecutableJobQueue {

    public OracleExecutableJobQueue(Config config) {
        super(config);
    }

    @Override
    public boolean createQueue(String taskTrackerNodeGroup) {
        createTable(readSqlFile("sql/oracle/lts_executable_job_queue.sql", getTableName(taskTrackerNodeGroup)));
        return true;
    }

    @Override
    public boolean removeQueue(String taskTrackerNodeGroup) {
        return new DropTableSql(getSqlTemplate())
                .drop(JobQueueUtils.getExecutableQueueName(taskTrackerNodeGroup))
                .doDrop();
    }

    @Override
    public boolean add(JobPo jobPo) {
        try {
            jobPo.setGmtModified(SystemClock.now());
            return super.add(getTableName(jobPo.getTaskTrackerNodeGroup()), jobPo);
        } catch (TableNotExistException e) {
            // 表不存在
            createQueue(jobPo.getTaskTrackerNodeGroup());
            add(jobPo);
        }
        return true;
    }

    @Override
    public boolean remove(String taskTrackerNodeGroup, String jobId) {
        return new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName(taskTrackerNodeGroup))
                .where("job_id = ?", jobId)
                .doDelete() == 1;
    }

    @Override
    public long countJob(String realTaskId, String taskTrackerNodeGroup) {
        return (Long) new SelectSql(getSqlTemplate())
                .select()
                .columns("COUNT(1)")
                .from()
                .table(getTableName(taskTrackerNodeGroup))
                .where("real_task_id = ?", realTaskId)
                .and("task_tracker_node_group = ?", taskTrackerNodeGroup)
                .single();
    }

    @Override
    public boolean removeBatch(String realTaskId, String taskTrackerNodeGroup) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .table(getTableName(taskTrackerNodeGroup))
                .where("real_task_id = ?", realTaskId)
                .and("task_tracker_node_group = ?", taskTrackerNodeGroup)
                .doDelete();
        return true;
    }

    @Override
    public void resume(JobPo jobPo) {
        new UpdateSql(getSqlTemplate())
                .update()
                .table(getTableName(jobPo.getTaskTrackerNodeGroup()))
                .set("is_running", false)
                .set("task_tracker_identity", null)
                .set("gmt_modified", SystemClock.now())
                .where("job_id=?", jobPo.getJobId())
                .doUpdate();
    }

    @Override
    public List<JobPo> getDeadJob(String taskTrackerNodeGroup, long deadline) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName(taskTrackerNodeGroup))
                .where("is_running = ?", true)
                .and("gmt_modified < ?", deadline)
                .list(RshHolder.JOB_PO_LIST_RSH);
    }

    @Override
    public JobPo getJob(String taskTrackerNodeGroup, String taskId) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .table(getTableName(taskTrackerNodeGroup))
                .where("task_id = ?", taskId)
                .and("task_tracker_node_group = ?", taskTrackerNodeGroup)
                .single(RshHolder.JOB_PO_RSH);
    }

    @Override
    protected String getTableName(JobQueueReq request) {
        if (StringUtils.isEmpty(request.getTaskTrackerNodeGroup())) {
            throw new IllegalArgumentException(" takeTrackerNodeGroup cat not be null");
        }
        return getTableName(request.getTaskTrackerNodeGroup());
    }

    private String getTableName(String taskTrackerNodeGroup) {
        return JobQueueUtils.getExecutableQueueName(taskTrackerNodeGroup);
    }
}
