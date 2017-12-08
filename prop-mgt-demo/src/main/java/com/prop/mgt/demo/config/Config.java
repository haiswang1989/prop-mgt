package com.prop.mgt.demo.config;

import org.springframework.stereotype.Component;

import com.prop.mgt.client.annotation.PropMgt;
import com.prop.mgt.client.annotation.PropMgtMethod;

@PropMgt(host="www.ebay.com.cn", filename="zk.properties")
@Component
public class Config {
    
    private String zkString;

    public String getZkString() {
        return zkString;
    }
    
    @PropMgtMethod(key="zookeeper.connection")
    public void setZkString(String zkString) {
        this.zkString = zkString;
    }
}
