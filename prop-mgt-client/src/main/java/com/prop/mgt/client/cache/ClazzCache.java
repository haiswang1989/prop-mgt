package com.prop.mgt.client.cache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.prop.mgt.client.callback.intf.IUpdateCallback;
import com.prop.mgt.client.model.HostConfigFile;

import lombok.Getter;

/**
 * 扫描的bean的缓存
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月1日 上午9:41:42
 */
public class ClazzCache {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(ClazzCache.class);
    
    //host_file ---> reflash的bean
    @Getter
    private Map<HostConfigFile, Class<?>> hostFile2PropMgtClazz = new HashMap<>();
    
    //reflash的bean ---> 回调的bean
    @Getter
    private Map<Class<?>, Set<Class<?>>> propMgtClass2PropCallbackClazz = new HashMap<>();
    
    //reflash的bean ---> method:fieldkey
    @Getter
    private Map<Class<?>, Map<Method, String>> propMgtClazz2FieldKey = new HashMap<>();
    
    private ApplicationContext applicationContext;
    
    public void setApplicationContext(ApplicationContext applicationContextArg) {
        this.applicationContext = applicationContextArg;
    }
    
    private ClazzCache() {
    }
    
    private static class Holder {
        public static final ClazzCache instance = new ClazzCache();
    }
    
    public static ClazzCache getInstance() {
        return Holder.instance;
    }
    
    /**
     * 
     * @param hostConfigFile
     * @param newJson
     */
    public void reflash(HostConfigFile hostConfigFile, Object newJson) {
        //更新field值
        Class<?> clazz = hostFile2PropMgtClazz.get(hostConfigFile);
        JSONObject jsonOject = JSONObject.parseObject(newJson.toString());
        Map<Method, String> methodField = propMgtClazz2FieldKey.get(clazz);
        for (Map.Entry<Method, String> entry : methodField.entrySet()) {
            Method method = entry.getKey();
            String key = entry.getValue();
            Object value = jsonOject.get(key);
            setField(method, value, clazz);
        }
        
        //update 回调
        Set<Class<?>> updateCallbackClasses = propMgtClass2PropCallbackClazz.get(clazz);
        if(CollectionUtils.isNotEmpty(updateCallbackClasses)) {
            for (Class<?> class1 : updateCallbackClasses) {
                Object targetBean = applicationContext.getBean(class1);
                if(targetBean instanceof IUpdateCallback) {
                    IUpdateCallback callback = (IUpdateCallback)targetBean;
                    callback.reload();
                } else {
                    LOGGER.error("{} is not assign from {}", class1, IUpdateCallback.class);
                }
            }
        }
    }
    
    /**
     * 
     * @param method
     * @param value
     * @param clazz
     */
    public void setField(Method method, Object value, Class<?> clazz) {
        Object bean = applicationContext.getBean(clazz);
        try {
            method.invoke(bean, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error(String.format("Set new value to field failed, method : %s , class : %s ,value ： %s", method, clazz, value), e);
        }
    }
}
