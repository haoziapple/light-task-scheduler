package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.monitor.access.domain.JobClientMDataPo;
import com.github.ltsopensource.monitor.access.face.JobClientMAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 13:53
 */
public class OracleJobClientMAccess extends OracleAbstractJdbcAccess implements JobClientMAccess {
    public OracleJobClientMAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<JobClientMDataPo> jobClientMDataPos) {
        if (CollectionUtils.isEmpty(jobClientMDataPos)) {
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
                        "SUBMIT_SUCCESS_NUM",
                        "SUBMIT_FAILED_NUM",
                        "FAIL_STORE_NUM",
                        "SUBMIT_FAIL_STORE_NUM",
                        "HANDLE_FEEDBACK_NUM");

        for (JobClientMDataPo jobClientMDataPo : jobClientMDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    jobClientMDataPo.getGmtCreated(),
                    jobClientMDataPo.getNodeGroup(),
                    jobClientMDataPo.getIdentity(),
                    jobClientMDataPo.getTimestamp(),
                    jobClientMDataPo.getSubmitSuccessNum(),
                    jobClientMDataPo.getSubmitFailedNum(),
                    jobClientMDataPo.getFailStoreNum(),
                    jobClientMDataPo.getSubmitFailStoreNum(),
                    jobClientMDataPo.getHandleFeedbackNum()
            );
        }
        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_job_client_mdata";
    }
}
