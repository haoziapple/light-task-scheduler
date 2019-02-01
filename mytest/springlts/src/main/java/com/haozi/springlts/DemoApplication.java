package com.haozi.springlts;

import com.github.ltsopensource.jobclient.JobClient;
import com.github.ltsopensource.spring.JobClientFactoryBean;
import com.haozi.springlts.config.ScheduleProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean(name = "jobClient")
    public JobClient getJobClient(ScheduleProperties scheduleProperties) throws Exception {
        JobClientFactoryBean factoryBean = new JobClientFactoryBean();
        factoryBean.setClusterName("test_cluster");
        factoryBean.setRegistryAddress(scheduleProperties.getRegistryAddress());
        factoryBean.setNodeGroup("test_jobClient");
        Properties configs = new Properties();
        configs.setProperty("job.fail.store", "leveldb");
        factoryBean.setDataPath("mesjobclient");
        factoryBean.setConfigs(configs);
        factoryBean.afterPropertiesSet();

        JobClient jobClient = factoryBean.getObject();
        jobClient.start();
        return jobClient;
    }

}