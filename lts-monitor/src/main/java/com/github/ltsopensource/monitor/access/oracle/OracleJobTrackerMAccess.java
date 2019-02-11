package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JobTrackerMDataPo;
import com.github.ltsopensource.monitor.access.face.JobTrackerMAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 10:49
 */
public class OracleJobTrackerMAccess extends OracleAbstractJdbcAccess implements JobTrackerMAccess {
    public OracleJobTrackerMAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<JobTrackerMDataPo> jobTrackerMDataPos) {
        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName().toUpperCase())
                .oracleColumns(
                        "ID",
                        "GMT_CREATED",
                        "IDENTITY",
                        "TIMESTAMP",
                        "RECEIVE_JOB_NUM",
                        "PUSH_JOB_NUM",
                        "EXE_SUCCESS_NUM",
                        "EXE_FAILED_NUM",
                        "EXE_LATER_NUM",
                        "EXE_EXCEPTION_NUM",
                        "FIX_EXECUTING_JOB_NUM");

        for (JobTrackerMDataPo jobTrackerMDataPo : jobTrackerMDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    jobTrackerMDataPo.getGmtCreated(),
                    jobTrackerMDataPo.getIdentity(),
                    jobTrackerMDataPo.getTimestamp(),
                    jobTrackerMDataPo.getReceiveJobNum(),
                    jobTrackerMDataPo.getPushJobNum(),
                    jobTrackerMDataPo.getExeSuccessNum(),
                    jobTrackerMDataPo.getExeFailedNum(),
                    jobTrackerMDataPo.getExeLaterNum(),
                    jobTrackerMDataPo.getExeExceptionNum(),
                    jobTrackerMDataPo.getFixExecutingJobNum()
            );
        }
        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_job_tracker_mdata";
    }
}
