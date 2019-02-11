package com.github.ltsopensource.admin.access.oracle;

import com.github.ltsopensource.admin.access.BackendAccessFactory;
import com.github.ltsopensource.admin.access.face.*;
import com.github.ltsopensource.core.cluster.Config;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-11 9:16
 */
public class OracleBackendAccessFactory implements BackendAccessFactory {
    @Override
    public BackendJobTrackerMAccess getJobTrackerMAccess(Config config) {
        return new OracleBackendJobTrackerMAccess(config);
    }

    @Override
    public BackendJobClientMAccess getBackendJobClientMAccess(Config config) {
        return new OracleBackendJobClientMAccess(config);
    }

    @Override
    public BackendJVMGCAccess getBackendJVMGCAccess(Config config) {
        return new OracleBackendJVMGCAccess(config);
    }

    @Override
    public BackendJVMMemoryAccess getBackendJVMMemoryAccess(Config config) {
        return new OracleBackendJVMMemoryAccess(config);
    }

    @Override
    public BackendJVMThreadAccess getBackendJVMThreadAccess(Config config) {
        return new OracleBackendJVMThreadAccess(config);
    }

    @Override
    public BackendNodeOnOfflineLogAccess getBackendNodeOnOfflineLogAccess(Config config) {
        return new OracleBackendNodeOnOfflineLogAccess(config);
    }

    @Override
    public BackendTaskTrackerMAccess getBackendTaskTrackerMAccess(Config config) {
        return new OracleBackendTaskTrackerMAccess(config);
    }
}
