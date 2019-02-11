package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendJobClientMAccess;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.admin.web.vo.NodeInfo;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JobClientMDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleJobClientMAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 9:42
 */
public class OracleBackendJobClientMAccess extends OracleJobClientMAccess implements BackendJobClientMAccess {
    public OracleBackendJobClientMAccess(Config config) {
        super(config);
    }

    @Override
    public void delete(MDataPaginationReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    @Override
    public List<JobClientMDataPo> querySum(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "SUM(SUBMIT_SUCCESS_NUM) AS submit_success_num",
                        "SUM(SUBMIT_FAILED_NUM) AS submit_failed_num",
                        "SUM(FAIL_STORE_NUM) AS fail_store_num",
                        "SUM(SUBMIT_FAIL_STORE_NUM) AS submit_fail_store_num",
                        "SUM(HANDLE_FEEDBACK_NUM) AS handle_feedback_num")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.JOB_CLIENT_SUM_M_DATA_RSH);
    }

    @Override
    public List<NodeInfo> getJobClients() {
        return new SelectSql(getSqlTemplate())
                .select()
                .columns("DISTINCT IDENTITY AS identity", "NODE_GROUP AS node_group")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .list(RshHandler.NODE_INFO_LIST_RSH);
    }

    public WhereSql buildWhereSql(MDataPaginationReq request) {
        return new WhereSql()
                .andOnNotNull("ID = ?", request.getId())
                .andOnNotEmpty("IDENTITY = ?", request.getIdentity())
                .andOnNotEmpty("NODE_GROUP = ?", request.getNodeGroup())
                .andBetween("TIMESTAMP", request.getStartTime(), request.getEndTime());
    }
}
