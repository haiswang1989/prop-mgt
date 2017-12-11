package com.prop.mgt.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zookeeper")  
@PropertySource("classpath:application.properties")
public class SrvConfig {
    
    private String zkConnectString;

    public String getZkConnectString() {
        return zkConnectString;
    }

    public void setZkConnectString(String zkConnectString) {
        this.zkConnectString = zkConnectString;
    }
}
