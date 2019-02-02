package com.github.ltsopensource.monitor.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.commons.utils.CollectionUtils;
import com.github.ltsopensource.monitor.access.domain.JVMThreadDataPo;
import com.github.ltsopensource.monitor.access.face.JVMThreadAccess;
import com.github.ltsopensource.store.jdbc.builder.InsertSql;

import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 11:48
 */
public class OracleJVMThreadAccess extends OracleAbstractJdbcAccess implements JVMThreadAccess {
    public OracleJVMThreadAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<JVMThreadDataPo> jvmThreadDataPos) {
        if (CollectionUtils.isEmpty(jvmThreadDataPos)) {
            return;
        }

        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName().toUpperCase())
                .oracleColumns(
                        "ID",
                        "gmt_created",
                        "identity",
                        "timestamp",
                        "node_type",
                        "node_group",
                        "daemon_thread_count",
                        "thread_count",
                        "total_started_thread_count",
                        "dead_locked_thread_count",
                        "process_cpu_time_rate");

        for (JVMThreadDataPo po : jvmThreadDataPos) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    po.getGmtCreated(),
                    po.getIdentity(),
                    po.getTimestamp(),
                    po.getNodeType().name(),
                    po.getNodeGroup(),
                    po.getDaemonThreadCount(),
                    po.getThreadCount(),
                    po.getTotalStartedThreadCount(),
                    po.getDeadLockedThreadCount(),
                    po.getProcessCpuTimeRate()
            );
        }

        insertSql.doBatchInsert();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_jvm_thread";
    }
}
