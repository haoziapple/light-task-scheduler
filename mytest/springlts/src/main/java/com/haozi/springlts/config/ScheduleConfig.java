package com.haozi.springlts.config;

import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.jobclient.JobClient;
import com.github.ltsopensource.jobclient.domain.Response;
import com.github.ltsopensource.spring.TaskTrackerAnnotationFactoryBean;
import com.github.ltsopensource.tasktracker.TaskTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Properties;

/**
 * @author wanghao
 * @Description
 * @date 2019-01-14 13:38
 */
@Configuration
@ConditionalOnProperty(name = "lts.schedule.enabled", havingValue = "true")
@Slf4j
public class ScheduleConfig implements CommandLineRunner {
    @Autowired
    private ApplicationContext ctx;
    /**
     * spring的bean工厂，用来注册定时任务task
     */
    @Autowired
    private DefaultListableBeanFactory beanFactory;
    /**
     * 用来操作触发或取消任务
     */
    @Autowired
    private JobClient jobClient;
    /**
     * 定时task配置
     */
    @Autowired
    private ScheduleProperties scheduleProperties;

    private TaskTracker getTaskTracker(String registryAdress, String nodeGroup, Class runnerClass) throws Exception {
        TaskTrackerAnnotationFactoryBean factoryBean = new TaskTrackerAnnotationFactoryBean();
        factoryBean.setApplicationContext(ctx);
        factoryBean.setClusterName("test_cluster");
        // 这边定义执行任务类
        factoryBean.setJobRunnerClass(runnerClass);
        // 任务task的标识，当你定义不同的任务时，这个也要定义自己的标识
        factoryBean.setNodeGroup(nodeGroup);
        factoryBean.setBizLoggerLevel("INFO");
        factoryBean.setRegistryAddress(registryAdress);
        factoryBean.setWorkThreads(20);
        Properties configs = new Properties();
        configs.setProperty("job.fail.store", "leveldb");
        factoryBean.setDataPath("tasktracker-" + nodeGroup);
        factoryBean.setConfigs(configs);
        factoryBean.afterPropertiesSet();
        factoryBean.start();
        return factoryBean.getObject();
    }

    /**
     * 服务启动后注册taskTracker，再将cron类型的任务启动初始化
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        scheduleProperties.getTasks().forEach((k, v) -> {
            // cron表达式非空，进行cron类任务初始化
            if (!StringUtils.isEmpty(v.getCron())) {
                log.warn("=====set cron task: " + k + ", " + v);
                // 根据配置提交新建任务
                Job job = new Job();
                // 任务task的标识，决定任务具体干什么
                job.setTaskTrackerNodeGroup(v.getId());
                job.setTaskId(v.getId());
                job.setCronExpression(v.getCron());
                job.setExtParams(v.getParamMap());
                // 是否依赖上一个执行周期(上一个执行周期任务执行完，才会进行下一周期的任务)
                job.setRelyOnPrevCycle(v.isRelyOnPrevCycle());
                job.setExtParams(v.getParamMap());
                job.setNeedFeedback(true);
                // 替换更新任务
                job.setReplaceOnExist(true);
                Response rsp = jobClient.submitJob(job);
                log.warn("=====submit job response: " + rsp);
            }
        });

        scheduleProperties.getTasks().forEach((k, v) -> {
            try {
                // 注册定时任务task
                beanFactory.registerSingleton(k, getTaskTracker(scheduleProperties.getRegistryAddress(), v.getId(), Class.forName(v.getRunner())));
            } catch (Exception e) {
                log.error("定时任务task注册失败", e);
            }
        });
    }
}
