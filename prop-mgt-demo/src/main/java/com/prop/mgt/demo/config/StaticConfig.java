package com.prop.mgt.demo.config;

import com.prop.mgt.client.annotation.PropMgt;
import com.prop.mgt.client.annotation.PropMgtMethod;

@PropMgt(host="www.ebay.com.cn", filename="zk.properties")
public class StaticConfig {
    
    private static String zkString;
    
    @PropMgtMethod(key="zookeeper.connection")
    public static void setZkString(String zkStringArg) {
        zkString = zkStringArg;
    }
    
    public static String getZkString() {
        return zkString;
    }
}
