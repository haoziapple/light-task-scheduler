package com.haozi.springlts;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;

import java.util.Date;

/**
 * @author wanghao
 * @Description
 * @date 2019-02-02 13:58
 */
public class MyJobRunner implements JobRunner {
    @Override
    public Result run(JobContext jobContext) throws Throwable {
        try {
            // TODO 业务逻辑
            System.out.println(new Date() + "===========MyJobRunner===========");
            // 获取业务参数
            System.out.println(new Date() + "===========Get shopId: " + jobContext.getJob().getParam("shopId"));
            // 会发送到 LTS (JobTracker上)
            jobContext.getBizLogger().info("testing running log");

        } catch (Exception e) {
            return new Result(Action.EXECUTE_FAILED, e.getMessage());
        }
        return new Result(Action.EXECUTE_SUCCESS, "testing success log");
    }
}
