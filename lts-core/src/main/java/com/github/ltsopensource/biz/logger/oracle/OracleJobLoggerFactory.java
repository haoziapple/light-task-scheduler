package com.github.ltsopensource.biz.logger.oracle;

import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.biz.logger.JobLoggerFactory;
import com.github.ltsopensource.core.cluster.Config;

/**
 * @author wanghao
 * @Description
 * @date 2018-11-05 19:30
 */
public class OracleJobLoggerFactory implements JobLoggerFactory {
    @Override
    public JobLogger getJobLogger(Config config) {
        return new OracleJobLogger(config);
    }
}
