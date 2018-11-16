package com.github.ltsopensource.queue.oracle;

import com.github.ltsopensource.core.AppContext;
import com.github.ltsopensource.core.cluster.Config;
import com.github.ltsopensource.queue.*;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-15 16:23
 */
public class OracleJobQueueFactory implements JobQueueFactory {
    @Override
    public CronJobQueue getCronJobQueue(Config config) {
        return new OracleCronJobQueue(config);
    }

    @Override
    public RepeatJobQueue getRepeatJobQueue(Config config) {
        return new OracleRepeatJobQueue(config);
    }

    @Override
    public ExecutableJobQueue getExecutableJobQueue(Config config) {
        return new OracleExecutableJobQueue(config);
    }

    @Override
    public ExecutingJobQueue getExecutingJobQueue(Config config) {
        return new OracleExecutingJobQueue(config);
    }

    @Override
    public JobFeedbackQueue getJobFeedbackQueue(Config config) {
        return null;
    }

    @Override
    public NodeGroupStore getNodeGroupStore(Config config) {
        return null;
    }

    @Override
    public SuspendJobQueue getSuspendJobQueue(Config config) {
        return null;
    }

    @Override
    public PreLoader getPreLoader(AppContext appContext) {
        return null;
    }
}
