package com.haozi.springlts.config;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

/**
 * @author wanghao
 * @Description
 * @date 2019-01-14 11:42
 */
@Data
@ToString
public class TaskProperties {
    /**
     * 任务id
     */
    private String id;
    /**
     * cron表达式
     */
    private String cron;

    /**
     * jobRunner类名(全路径)
     */
    private String runner;
    /**
     * 是否依赖上一个执行周期(上一个执行周期任务执行完，才会进行下一周期的任务)
     */
    private boolean relyOnPrevCycle = true;
    /**
     * 任务参数
     */
    private Map<String, String> paramMap;
}
