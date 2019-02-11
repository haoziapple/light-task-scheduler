package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendJVMMemoryAccess;
import com.github.ltsopensource.admin.request.JvmDataReq;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JVMMemoryDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleJVMMemoryAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 10:17
 */
public class OracleBackendJVMMemoryAccess extends OracleJVMMemoryAccess implements BackendJVMMemoryAccess {
    public OracleBackendJVMMemoryAccess(Config config) {
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
    public List<JVMMemoryDataPo> queryAvg(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "AVG(HEAP_MEMORY_COMMITTED) AS heap_memory_committed",
                        "AVG(HEAP_MEMORY_INIT) AS heap_memory_init",
                        "AVG(HEAP_MEMORY_MAX) AS heap_memory_max",
                        "AVG(HEAP_MEMORY_USED) AS heap_memory_used",
                        "AVG(NON_HEAP_MEMORY_COMMITTED) AS non_heap_memory_committed",
                        "AVG(NON_HEAP_MEMORY_INIT) AS non_heap_memory_init",
                        "AVG(NON_HEAP_MEMORY_MAX) AS non_heap_memory_max",
                        "AVG(NON_HEAP_MEMORY_USED) AS non_heap_memory_used",
                        "AVG(PERM_GEN_COMMITTED) AS perm_gen_committed",
                        "AVG(PERM_GEN_INIT) AS perm_gen_init",
                        "AVG(PERM_GEN_MAX) AS perm_gen_max",
                        "AVG(PERM_GEN_USED) AS perm_gen_used",
                        "AVG(OLD_GEN_COMMITTED) AS old_gen_committed",
                        "AVG(OLD_GEN_INIT) AS old_gen_init",
                        "AVG(OLD_GEN_MAX) AS old_gen_max",
                        "AVG(OLD_GEN_USED) AS old_gen_used",
                        "AVG(EDEN_SPACE_COMMITTED) AS eden_space_committed",
                        "AVG(EDEN_SPACE_INIT) AS eden_space_init",
                        "AVG(EDEN_SPACE_MAX) AS eden_space_max",
                        "AVG(EDEN_SPACE_USED) AS eden_space_used",
                        "AVG(SURVIVOR_COMMITTED) AS survivor_committed",
                        "AVG(SURVIVOR_INIT) AS survivor_init",
                        "AVG(SURVIVOR_MAX) AS survivor_max",
                        "AVG(SURVIVOR_USED) AS survivor_used")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.JVM_MEMORY_SUM_M_DATA_RSH);
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
