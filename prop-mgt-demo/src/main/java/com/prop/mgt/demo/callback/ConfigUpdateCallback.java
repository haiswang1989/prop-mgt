package com.prop.mgt.demo.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.prop.mgt.client.annotation.PropMgtUpdateCallback;
import com.prop.mgt.client.callback.intf.IUpdateCallback;
import com.prop.mgt.demo.config.Config;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月6日 下午5:11:04
 */
@PropMgtUpdateCallback(classes={Config.class})
@Component
public class ConfigUpdateCallback implements IUpdateCallback {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ConfigUpdateCallback.class);
    
    private ApplicationContext applicationContext;
    
    public ConfigUpdateCallback(ApplicationContext applicationContextArg) {
        this.applicationContext = applicationContextArg;
    }
    
    @Override
    public void reload() {
        LOGGER.info("Config info is change.");
        Config config = applicationContext.getBean(Config.class);
        System.out.println("New : " + config.getZkString());
    }
}
