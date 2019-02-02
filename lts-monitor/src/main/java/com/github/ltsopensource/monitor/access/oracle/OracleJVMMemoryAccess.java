package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.monitor.access.domain.JVMMemoryDataPo;
import com.github.ltsopensource.monitor.access.face.JVMMemoryAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 11:40
 */
public class OracleJVMMemoryAccess extends OracleAbstractJdbcAccess implements JVMMemoryAccess {

    public OracleJVMMemoryAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<JVMMemoryDataPo> jvmMemoryDataPos) {
        if (CollectionUtils.isEmpty(jvmMemoryDataPos)) {
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
                        "HEAP_MEMORY_COMMITTED",
                        "HEAP_MEMORY_INIT",
                        "HEAP_MEMORY_MAX",
                        "HEAP_MEMORY_USED",
                        "NON_HEAP_MEMORY_COMMITTED",
                        "NON_HEAP_MEMORY_INIT",
                        "NON_HEAP_MEMORY_MAX",
                        "NON_HEAP_MEMORY_USED",
                        "PERM_GEN_COMMITTED",
                        "PERM_GEN_INIT",
                        "PERM_GEN_MAX",
                        "PERM_GEN_USED",
                        "OLD_GEN_COMMITTED",
                        "OLD_GEN_INIT",
                        "OLD_GEN_MAX",
                        "OLD_GEN_USED",
                        "EDEN_SPACE_COMMITTED",
                        "EDEN_SPACE_INIT",
                        "EDEN_SPACE_MAX",
                        "EDEN_SPACE_USED",
                        "SURVIVOR_COMMITTED",
                        "SURVIVOR_INIT",
                        "SURVIVOR_MAX",
                        "SURVIVOR_USED");
        for (JVMMemoryDataPo po : jvmMemoryDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    po.getGmtCreated(),
                    po.getIdentity(),
                    po.getTimestamp(),
                    po.getNodeType().name(),
                    po.getNodeGroup(),
                    po.getHeapMemoryCommitted(),
                    po.getHeapMemoryInit(),
                    po.getHeapMemoryMax(),
                    po.getHeapMemoryUsed(),
                    po.getNonHeapMemoryCommitted(),
                    po.getNonHeapMemoryInit(),
                    po.getNonHeapMemoryMax(),
                    po.getNonHeapMemoryUsed(),
                    po.getPermGenCommitted(),
                    po.getPermGenInit(),
                    po.getPermGenMax(),
                    po.getPermGenUsed(),
                    po.getOldGenCommitted(),
                    po.getOldGenInit(),
                    po.getOldGenMax(),
                    po.getOldGenUsed(),
                    po.getEdenSpaceCommitted(),
                    po.getEdenSpaceInit(),
                    po.getEdenSpaceMax(),
                    po.getEdenSpaceUsed(),
                    po.getSurvivorCommitted(),
                    po.getSurvivorInit(),
                    po.getSurvivorMax(),
                    po.getSurvivorUsed()
            );
        }

        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_jvm_memory";
    }
}
