package com.haozi.springlts.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author wanghao
 * @Description
 * @date 2019-01-14 11:43
 */
@ConfigurationProperties(prefix = "lts.schedule")
@Component
@Data
@ToString
public class ScheduleProperties {
    private Boolean enabled;

    private String registryAddress;

    private Map<String, TaskProperties> tasks;
}
