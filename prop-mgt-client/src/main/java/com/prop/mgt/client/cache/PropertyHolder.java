package com.prop.mgt.client.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.prop.mgt.client.model.HostConfigFile;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月7日 下午4:58:11
 */
public class PropertyHolder {
    
    //all properties info
    private static final Map<String, Map<String, Map<String, Object>>> allProperties = new HashMap<>();
    
    /**
     * 
     * @param host
     * @param propertyFile
     * @param jsonObject
     */
    public static void put(String host, String propertyFile, JSONObject jsonObject) {
        Map<String, Map<String, Object>> file2Properties = allProperties.get(host);
        if(null==file2Properties) {
            file2Properties = new HashMap<>();
        }
        
        file2Properties.put(propertyFile, jsonObject);
        allProperties.put(host, file2Properties);
    }
    
    /**
     * 
     * @param host
     * @param propertyFile
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProperty(String host, String propertyFile, String key) {
        Map<String, Object> properties = getProperties(host, propertyFile);
        Object value = properties.get(key);
        if(null!=value) {
            return (T)value;
        }
        
        return null;
    }
    
    /**
     * 
     * @param host
     * @param propertyFile
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getProperties(String host, String propertyFile) {
        Map<String, Map<String, Object>> file2Properties = allProperties.get(host);
        if(null!=file2Properties) {
            Map<String, Object> properties = file2Properties.get(propertyFile);
            if(null!=properties) {
                return properties;
            }
        }
        
        return (Map<String, Object>)Collections.EMPTY_MAP;
    }
    
    /**
     * 刷新
     * @param hostConfigFile
     * @param newJson
     */
    public static void reflash(HostConfigFile hostConfigFile, Object newJson) {
        JSONObject jsonOject = JSONObject.parseObject(newJson.toString());
        put(hostConfigFile.getHost(), hostConfigFile.getFileName(), jsonOject);
    }
    
    /**
     * 删除
     * @param hostConfigFile
     */
    public static void delete(HostConfigFile hostConfigFile) {
        String host = hostConfigFile.getHost();
        String filename = hostConfigFile.getFileName();
        Map<String, Map<String, Object>> map1 = allProperties.get(host);
        if(null!=map1) {
            map1.remove(filename);
        }
    }
}
