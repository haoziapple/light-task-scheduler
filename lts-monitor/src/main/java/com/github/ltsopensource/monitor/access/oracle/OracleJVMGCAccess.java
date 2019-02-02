package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.monitor.access.domain.JVMGCDataPo;
import com.github.ltsopensource.monitor.access.face.JVMGCAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 11:33
 */
public class OracleJVMGCAccess extends OracleAbstractJdbcAccess implements JVMGCAccess {
    public OracleJVMGCAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<JVMGCDataPo> jvmGCDataPos) {
        if (CollectionUtils.isEmpty(jvmGCDataPos)) {
            return;
        }

        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName().toUpperCase())
                .oracleColumns(
                        "ID",
                        "GMT_CREATED",
                        "IDENTITY",
                        "TIMESTAMP",
                        "NODE_TYPE",
                        "NODE_GROUP",
                        "YOUNG_GC_COLLECTION_COUNT",
                        "YOUNG_GC_COLLECTION_TIME",
                        "FULL_GC_COLLECTION_COUNT",
                        "FULL_GC_COLLECTION_TIME",
                        "SPAN_YOUNG_GC_COLLECTION_COUNT",
                        "SPAN_YOUNG_GC_COLLECTION_TIME",
                        "SPAN_FULL_GC_COLLECTION_COUNT",
                        "SPAN_FULL_GC_COLLECTION_TIME");

        for (JVMGCDataPo po : jvmGCDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    po.getGmtCreated(),
                    po.getIdentity(),
                    po.getTimestamp(),
                    po.getNodeType().name(),
                    po.getNodeGroup(),
                    po.getYoungGCCollectionCount(),
                    po.getYoungGCCollectionTime(),
                    po.getFullGCCollectionCount(),
                    po.getFullGCCollectionTime(),
                    po.getSpanYoungGCCollectionCount(),
                    po.getSpanYoungGCCollectionTime(),
                    po.getSpanFullGCCollectionCount(),
                    po.getSpanFullGCCollectionTime()
            );
        }

        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_jvm_gc";
    }
}
