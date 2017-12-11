package com.prop.mgt.core.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prop.mgt.core.config.SrvConfig;

@RestController
@EnableAutoConfiguration
public class PropMgtSrv {
    
    @Autowired
    private SrvConfig srvConfig;
    
    @RequestMapping("/helloworld")
    public String greeting() {
        return "hello world.";
    }
    
    
    @RequestMapping("/config")
    public String config() {
        return srvConfig.getZkConnectString();
    }
}
