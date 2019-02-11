package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendJVMThreadAccess;
import com.github.ltsopensource.admin.request.JvmDataReq;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JVMThreadDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleJVMThreadAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 10:23
 */
public class OracleBackendJVMThreadAccess extends OracleJVMThreadAccess implements BackendJVMThreadAccess {
    public OracleBackendJVMThreadAccess(Config config) {
        super(config);
    }

    @Override
    public void delete(JvmDataReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    @Override
    public List<JVMThreadDataPo> queryAvg(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "AVG(DAEMON_THREAD_COUNT) AS daemon_thread_count",
                        "AVG(THREAD_COUNT) AS thread_count",
                        "AVG(TOTAL_STARTED_THREAD_COUNT) AS total_started_thread_count",
                        "AVG(DEAD_LOCKED_THREAD_COUNT) AS dead_locked_thread_count",
                        "AVG(PROCESS_CPU_TIME_RATE) AS process_cpu_time_rate")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.JVM_THREAD_SUM_M_DATA_RSH);
    }

    public WhereSql buildWhereSql(JvmDataReq req) {
        return new WhereSql()
                .andOnNotEmpty("IDENTITY = ?", req.getIdentity())
                .andBetween("TIMESTAMP", req.getStartTime(), req.getEndTime());

    }

    public WhereSql buildWhereSql(MDataPaginationReq request) {
        return new WhereSql()
                .andOnNotNull("ID = ?", request.getId())
                .andOnNotEmpty("IDENTITY = ?", request.getIdentity())
                .andOnNotEmpty("NODE_GROUP = ?", request.getNodeGroup())
                .andBetween("TIMESTAMP", request.getStartTime(), request.getEndTime());
    }
}
