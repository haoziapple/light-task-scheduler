package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendTaskTrackerMAccess;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.admin.web.vo.NodeInfo;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.TaskTrackerMDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleTaskTrackerMAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 11:03
 */
public class OracleBackendTaskTrackerMAccess extends OracleTaskTrackerMAccess implements BackendTaskTrackerMAccess {
    public OracleBackendTaskTrackerMAccess(Config config) {
        super(config);
    }

    @Override
    public List<TaskTrackerMDataPo> querySum(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "SUM(EXE_SUCCESS_NUM) AS exe_success_num",
                        "SUM(EXE_FAILED_NUM) AS exe_failed_num",
                        "SUM(EXE_LATER_NUM) AS exe_later_num",
                        "SUM(EXE_EXCEPTION_NUM) AS exe_exception_num",
                        "SUM(TOTAL_RUNNING_TIME) AS total_running_time")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.TASK_TRACKER_SUM_M_DATA_RSH);
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
    public List<NodeInfo> getTaskTrackers() {
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
