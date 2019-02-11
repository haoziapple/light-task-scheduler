package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.SnowFlakeWorker;
import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.domain.NodeOnOfflineLog;
import com.github.ltsopensource.admin.access.face.BackendNodeOnOfflineLogAccess;
import com.github.ltsopensource.admin.request.NodeOnOfflineLogPaginationReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.oracle.OracleAbstractJdbcAccess;
import com.github.ltsopensource.store.jdbc.builder.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 10:29
 */
public class OracleBackendNodeOnOfflineLogAccess extends OracleAbstractJdbcAccess implements BackendNodeOnOfflineLogAccess {
    public OracleBackendNodeOnOfflineLogAccess(Config config) {
        super(config);
    }

    @Override
    public void insert(List<NodeOnOfflineLog> nodeOnOfflineLogs) {
        InsertSql insertSql = new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName().toUpperCase())
                .oracleColumns(
                        "ID",
                        "LOG_TIME",
                        "EVENT",
                        "NODE_TYPE",
                        "CLUSTER_NAME",
                        "IP",
                        "PORT",
                        "HOST_NAME",
                        "NODE_GROUP",
                        "CREATE_TIME",
                        "THREADS",
                        "IDENTITY",
                        "HTTP_CMD_PORT");
        for (NodeOnOfflineLog nodeOnOfflineLog : nodeOnOfflineLogs) {
            insertSql.values(
                    SnowFlakeWorker.getId(),
                    nodeOnOfflineLog.getLogTime().getTime(),
                    nodeOnOfflineLog.getEvent(),
                    nodeOnOfflineLog.getNodeType().name(),
                    nodeOnOfflineLog.getClusterName(),
                    nodeOnOfflineLog.getIp(),
                    nodeOnOfflineLog.getPort(),
                    nodeOnOfflineLog.getHostName(),
                    nodeOnOfflineLog.getGroup(),
                    nodeOnOfflineLog.getCreateTime(),
                    nodeOnOfflineLog.getThreads(),
                    nodeOnOfflineLog.getIdentity(),
                    nodeOnOfflineLog.getHttpCmdPort()
            );
        }
        insertSql.doBatchInsert();
    }

    @Override
    public List<NodeOnOfflineLog> select(NodeOnOfflineLogPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("LOG_TIME AS log_time",
                        "EVENT AS event",
                        "NODE_TYPE AS node_type",
                        "CLUSTER_NAME AS cluster_name",
                        "IP AS ip",
                        "PORT AS port",
                        "HOST_NAME AS host_name",
                        "NODE_GROUP AS node_group",
                        "CREATE_TIME AS create_time",
                        "THREADS AS threads",
                        "IDENTITY AS identity",
                        "HTTP_CMD_PORT AS http_cmd_port")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .orderBy()
                .column("LOG_TIME", OrderByType.DESC)
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.ORACLE_NODE_ON_OFFLINE_LOG_LIST_RSH);
    }

    @Override
    public Long count(NodeOnOfflineLogPaginationReq request) {
        return ((BigDecimal) new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .single()).longValue();
    }

    @Override
    public void delete(NodeOnOfflineLogPaginationReq request) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .doDelete();
    }

    @Override
    protected String getTableName() {
        return "lts_admin_node_onoffline_log";
    }

    private WhereSql buildWhereSql(NodeOnOfflineLogPaginationReq request){
        return new WhereSql()
                .andOnNotEmpty("IDENTITY = ?", request.getIdentity())
                .andOnNotEmpty("NODE_GROUP = ?", request.getGroup())
                .andOnNotEmpty("EVENT = ?", request.getEvent())
                .andBetween("LOG_TIME", request.getStartLogTime().getTime(), request.getEndLogTime().getTime());
    }
}
