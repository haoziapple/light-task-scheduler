package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendJVMGCAccess;
import com.github.ltsopensource.admin.request.JvmDataReq;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JVMGCDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleJVMGCAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 9:57
 */
public class OracleBackendJVMGCAccess extends OracleJVMGCAccess implements BackendJVMGCAccess {
    public OracleBackendJVMGCAccess(Config config) {
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
    public List<JVMGCDataPo> queryAvg(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "AVG(YOUNG_GC_COLLECTION_COUNT) AS young_gc_collection_count",
                        "AVG(YOUNG_GC_COLLECTION_TIME) AS young_gc_collection_time",
                        "AVG(FULL_GC_COLLECTION_COUNT) AS full_gc_collection_count",
                        "AVG(FULL_GC_COLLECTION_TIME) AS full_gc_collection_time",
                        "AVG(SPAN_YOUNG_GC_COLLECTION_COUNT) AS span_young_gc_collection_count",
                        "AVG(SPAN_YOUNG_GC_COLLECTION_TIME) AS span_young_gc_collection_time",
                        "AVG(SPAN_FULL_GC_COLLECTION_COUNT) span_full_gc_collection_count",
                        "AVG(SPAN_FULL_GC_COLLECTION_TIME) span_full_gc_collection_time")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.JVM_GC_SUM_M_DATA_RSH);
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
