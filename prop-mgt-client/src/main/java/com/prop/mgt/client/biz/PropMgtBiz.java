package com.prop.mgt.client.biz;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.prop.mgt.client.cache.PropertyHolder;
import com.prop.mgt.client.model.HostConfigFile;
import com.prop.mgt.client.zookeeper.ZkUtils;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月4日 下午5:03:46
 */
public class PropMgtBiz {
    
    /**
     * 
     * @param host
     * @param propertyFilename
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getProperties(HostConfigFile hostConfigFile) {
        String date = ZkUtils.getDate(hostConfigFile.getHost(), hostConfigFile.getFileName());
        if(null==date) {
            return (Map<String, Object>)Collections.EMPTY_MAP;
        }
        
        return JSON.parseObject(date);
    }
    
    /**
     * 
     * @param host
     * @return
     */
    public static void initPropertyHolder(String host) {
        List<String> propertyFiles = ZkUtils.getChildNode(host);
        for (String propertyFile : propertyFiles) {
            String date = ZkUtils.getDate(host, propertyFile);
            PropertyHolder.put(host, propertyFile, JSONObject.parseObject(date));
        }
    }
}
