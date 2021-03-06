package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.RshHandler;
import com.github.ltsopensource.admin.access.face.BackendJobTrackerMAccess;
import com.github.ltsopensource.admin.request.MDataPaginationReq;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.monitor.access.domain.JobTrackerMDataPo;
import com.github.ltsopensource.monitor.access.oracle.OracleJobTrackerMAccess;
import com.github.ltsopensource.store.jdbc.builder.DeleteSql;
import com.github.ltsopensource.store.jdbc.builder.SelectSql;
import com.github.ltsopensource.store.jdbc.builder.WhereSql;
import com.github.ltsopensource.store.jdbc.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 9:20
 */
public class OracleBackendJobTrackerMAccess extends OracleJobTrackerMAccess implements BackendJobTrackerMAccess {

    public OracleBackendJobTrackerMAccess(Config config) {
        super(config);
    }

    @Override
    public List<JobTrackerMDataPo> querySum(MDataPaginationReq request) {
        return new SelectSql(getSqlTemplate())
                .rowNumStart()
                .select()
                .columns("TIMESTAMP AS timestamp",
                        "SUM(RECEIVE_JOB_NUM) AS receive_job_num",
                        "SUM(PUSH_JOB_NUM) AS push_job_num" ,
                        "SUM(EXE_SUCCESS_NUM) AS exe_success_num" ,
                        "SUM(EXE_FAILED_NUM) AS exe_failed_num" ,
                        "SUM(EXE_LATER_NUM) AS exe_later_num" ,
                        "SUM(EXE_EXCEPTION_NUM) AS exe_exception_num" ,
                        "SUM(FIX_EXECUTING_JOB_NUM) AS fix_executing_job_num")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .whereSql(buildWhereSql(request))
                .groupBy(" TIMESTAMP ORDER BY TIMESTAMP ASC ")
                .rowNumEnd(request.getStart(), request.getLimit())
                .list(RshHandler.JOB_TRACKER_SUM_M_DATA_RSH);
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
    public List<String> getJobTrackers() {
        return new SelectSql(getSqlTemplate())
                .select()
                .columns("DISTINCT IDENTITY AS identity")
                .from()
                .oracleTable(getTableName().toUpperCase())
                .list(new ResultSetHandler<List<String>>() {
                    @Override
                    public List<String> handle(ResultSet rs) throws SQLException {
                        List<String> list = new ArrayList<String>();
                        while (rs.next()) {
                            list.add(rs.getString("identity"));
                        }
                        return list;
                    }
                });
    }

    public WhereSql buildWhereSql(MDataPaginationReq request) {
        return new WhereSql()
                .andOnNotNull("ID = ?", request.getId())
                .andOnNotEmpty("IDENTITY = ?", request.getIdentity())
                .andBetween("TIMESTAMP", request.getStartTime(), request.getEndTime());
    }
}
