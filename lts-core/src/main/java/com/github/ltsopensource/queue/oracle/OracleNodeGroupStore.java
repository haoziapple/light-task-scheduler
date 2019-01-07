package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.admin.response.PaginationRsp;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.core.cluster.NodeType;
import com.github.ltsopensource.core.domain.NodeGroupGetReq;
import com.github.ltsopensource.core.support.JobQueueUtils;
import com.github.ltsopensource.core.support.SystemClock;
import com.github.ltsopensource.queue.NodeGroupStore;
import com.github.ltsopensource.queue.domain.NodeGroupPo;
import com.github.ltsopensource.queue.mysql.support.RshHolder;
import com.github.ltsopensource.store.jdbc.JdbcAbstractAccess;
import com.github.ltsopensource.store.jdbc.builder.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-19 15:43
 */
public class OracleNodeGroupStore extends JdbcAbstractAccess implements NodeGroupStore {
    public OracleNodeGroupStore(Config config) {
        super(config);
        if(!isOracleTableExist(getTableName())) {
            createTable(readSqlFile("sql/oracle/lts_node_group_store.sql", getTableName()));
        }
    }

    @Override
    public void addNodeGroup(NodeType nodeType, String name) {
        BigDecimal count = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName())
                .where("NODE_TYPE = ?", nodeType.name())
                .and("NAME = ?", name)
                .single();
        if (count.longValue() > 0) {
            //  already exist
            return;
        }
        new InsertSql(getSqlTemplate())
                .oracleInsert(getTableName())
                .oracleColumns("NODE_TYPE", "NAME", "GMT_CREATED")
                .values(nodeType.name(), name, SystemClock.now())
                .doInsert();
    }

    @Override
    public void removeNodeGroup(NodeType nodeType, String name) {
        new DeleteSql(getSqlTemplate())
                .delete()
                .from()
                .oracleTable(getTableName())
                .where("NODE_TYPE = ?", nodeType.name())
                .and("NAME = ?", name)
                .doDelete();
    }

    @Override
    public List<NodeGroupPo> getNodeGroup(NodeType nodeType) {
        return new SelectSql(getSqlTemplate())
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .where("NODE_TYPE = ?", nodeType.name())
                .list(RshHolder.NODE_GROUP_LIST_RSH);
    }

    @Override
    public PaginationRsp<NodeGroupPo> getNodeGroup(NodeGroupGetReq request) {
        PaginationRsp<NodeGroupPo> response = new PaginationRsp<NodeGroupPo>();

        Long results = new SelectSql(getSqlTemplate())
                .select()
                .columns("count(1)")
                .from()
                .oracleTable(getTableName())
                .whereSql(
                        new WhereSql()
                                .andOnNotNull("NODE_TYPE = ?", request.getNodeType() == null ? null : request.getNodeType().name())
                                .andOnNotEmpty("NAME = ?", request.getNodeGroup())
                )
                .single();
        response.setResults(results.intValue());
        if (results == 0) {
            return response;
        }

        List<NodeGroupPo> rows = new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .all()
                .from()
                .oracleTable(getTableName())
                .whereSql(
                        new WhereSql()
                                .andOnNotNull("NODE_TYPE = ?", request.getNodeType() == null ? null : request.getNodeType().name())
                                .andOnNotEmpty("NAME = ?", request.getNodeGroup())
                )
                .orderBy()
                .column("GMT_CREATED", OrderByType.DESC)
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHolder.NODE_GROUP_LIST_RSH);

        response.setRows(rows);

        return response;
    }

    private String getTableName() {
        return JobQueueUtils.NODE_GROUP_STORE.toUpperCase();
    }
}
