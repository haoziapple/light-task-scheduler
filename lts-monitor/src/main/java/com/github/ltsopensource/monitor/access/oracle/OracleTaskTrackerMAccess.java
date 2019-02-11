package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.monitor.access.domain.TaskTrackerMDataPo;
import com.github.ltsopensource.monitor.access.face.TaskTrackerMAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 11:24
 */
public class OracleTaskTrackerMAccess extends OracleAbstractJdbcAccess implements TaskTrackerMAccess {
    public OracleTaskTrackerMAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<TaskTrackerMDataPo> taskTrackerMDataPos) {
        if (CollectionUtils.isEmpty(taskTrackerMDataPos)) {
            return;
        }

        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName().toUpperCase())
                .oracleColumns(
                        "ID",
                        "GMT_CREATED",
                        "NODE_GROUP",
                        "IDENTITY",
                        "TIMESTAMP",
                        "EXE_SUCCESS_NUM",
                        "EXE_FAILED_NUM",
                        "EXE_LATER_NUM",
                        "EXE_EXCEPTION_NUM",
                        "TOTAL_RUNNING_TIME");

        for (TaskTrackerMDataPo taskTrackerMDataPo : taskTrackerMDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    taskTrackerMDataPo.getGmtCreated(),
                    taskTrackerMDataPo.getNodeGroup(),
                    taskTrackerMDataPo.getIdentity(),
                    taskTrackerMDataPo.getTimestamp(),
                    taskTrackerMDataPo.getExeSuccessNum(),
                    taskTrackerMDataPo.getExeFailedNum(),
                    taskTrackerMDataPo.getExeLaterNum(),
                    taskTrackerMDataPo.getExeExceptionNum(),
                    taskTrackerMDataPo.getTotalRunningTime()
            );
        }

        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_task_tracker_mdata";
    }
}
