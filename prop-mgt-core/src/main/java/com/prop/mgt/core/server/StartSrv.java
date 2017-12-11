package com.prop.mgt.core.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartSrv {
    
    @Value("${zookeeper.connection}")
    private String zkConnetion;
    
    public static void main(String[] args) {
        
        SpringApplication.run(StartSrv.class, args);
        
    }
}
