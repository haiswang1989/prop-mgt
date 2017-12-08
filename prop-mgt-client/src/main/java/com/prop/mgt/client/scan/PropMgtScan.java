package com.prop.mgt.client.scan;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.collect.Sets;
import com.prop.mgt.client.annotation.PropMgt;
import com.prop.mgt.client.annotation.PropMgtMethod;
import com.prop.mgt.client.annotation.PropMgtUpdateCallback;
import com.prop.mgt.client.biz.PropMgtBiz;
import com.prop.mgt.client.cache.ClazzCache;
import com.prop.mgt.client.common.PropMgtUtils;
import com.prop.mgt.client.exception.PropMgtException;
import com.prop.mgt.client.model.HostConfigFile;
import com.prop.mgt.client.util.PackageScanner;
import com.prop.mgt.client.zookeeper.ZkUtils;
import com.prop.mgt.client.zookeeper.listener.PropertyUpdateListener;

import lombok.Setter;

/**
 * 扫描被prop-mgt的annotation注释的class和字段 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年11月30日 下午3:38:31
 */
public class PropMgtScan implements InitializingBean,ApplicationContextAware {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(PropMgtScan.class);
    
    private ApplicationContext applicationContext;
    
    private ZkClient zkClient;
    
    //需要扫描的包名
    @Setter
    private Set<String> packages;
    
    public PropMgtScan() {
        
    }
    
    //TODO For test
    public PropMgtScan(Set<String> packagesArg) {
        this.packages = packagesArg;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        //扫描package
        doScanPackage();
        //做一些资源初始化
        doInit();
        //设置bean的field值
        setBeanProperties();
        //
        addZkListener();
        //初始化PropertyHolder
        initPropertyHolder();
    }
    
    /**
     * 
     */
    private void initPropertyHolder() {
        Map<HostConfigFile, Class<?>> hostFile2PropMgtClazz = ClazzCache.getInstance().getHostFile2PropMgtClazz();
        Set<HostConfigFile> hostConfigFiles = hostFile2PropMgtClazz.keySet();
        Set<String> hosts = new HashSet<>();
        for (HostConfigFile hostConfigFile : hostConfigFiles) {
            hosts.add(hostConfigFile.getHost());
        }
        
        for (String host : hosts) {
            PropMgtBiz.initPropertyHolder(host);
        }
    }
    
    /**
     * 对配置变化进行监听
     */
    private void addZkListener() {
        Map<HostConfigFile, Class<?>> hostFile2PropMgt = ClazzCache.getInstance().getHostFile2PropMgtClazz();
        Set<HostConfigFile> hostConfigFiles = hostFile2PropMgt.keySet();
        for (HostConfigFile hostConfigFile : hostConfigFiles) {
            String fullPath = PropMgtUtils.getFullPath(hostConfigFile.getHost(), hostConfigFile.getFileName());
            zkClient.subscribeDataChanges(fullPath, new PropertyUpdateListener());
        }
    }
    
    /**
     * 
     */
    private void doInit() {
        //读取配置信息
        Properties prop = new Properties();
        InputStream is = null; 
        try {
            is = PropMgtScan.class.getClassLoader().getResourceAsStream("application.properties"); 
            prop.load(is);
        } catch (IOException e) {
            LOGGER.error("Read config file failed, JVM will exit.", e);
            System.exit(-1);
        } finally {
            if(null!=is) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        
        String zkConnection = prop.getProperty("zookeeper.connection");
        //初始化zkclient
        zkClient = new ZkClient(zkConnection);
        ZkUtils.setZkClient(zkClient);
        
        //
        ClazzCache.getInstance().setApplicationContext(applicationContext);
    }
    
    /**
     * 
     */
    private void setBeanProperties() {
        Map<HostConfigFile, Class<?>> hostFile2PropMgtClass = ClazzCache.getInstance().getHostFile2PropMgtClazz();
        Map<Class<?>, Map<Method, String>> propMgtClass2FieldKey = ClazzCache.getInstance().getPropMgtClazz2FieldKey();
        for (Map.Entry<HostConfigFile, Class<?>> entry : hostFile2PropMgtClass.entrySet()) {
            HostConfigFile hostConfigFile = entry.getKey();
            Map<String, Object> keyValues = PropMgtBiz.getProperties(hostConfigFile);
            
            Class<?> clazz = entry.getValue();
            Map<Method, String> method2FieldKey = propMgtClass2FieldKey.get(clazz);
            for (Map.Entry<Method, String> entry1 : method2FieldKey.entrySet()) {
                Method method = entry1.getKey();
                String key = entry1.getValue();
                Object value = keyValues.get(key);
                setField(method, value, clazz);
            }
        }
    }
    
    /**
     * 设置Field的值
     * @param method
     * @param value
     * @param clazz
     */
    private void setField(Method method, Object value, Class<?> clazz) {
        Object bean = applicationContext.getBean(clazz);
        try {
            method.invoke(bean, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            LOGGER.error("Invoke set method failed", e);
        }
    }
    
    /**
     * 扫描 package,并进行过滤
     */
    private void doScanPackage() {
        boolean debugEnabled = LOGGER.isDebugEnabled();
        Set<Class<?>> classes = PackageScanner.scan(packages);
        Set<Class<?>> candidates = Sets.newHashSet();
        for (Class<?> clazz : classes) {
            if(isCandidateClazz(clazz)) {
                if(debugEnabled) {
                    LOGGER.debug("Identified candidate class : " + clazz);
                }
                candidates.add(clazz);
            } else {
                if(debugEnabled) {
                    LOGGER.debug("Ignored because has no prop mgt annotation class : " + clazz);
                }
            }
        }
        
        tryCacheClasses(candidates);
    }
    
    /**
     * 将候选的clazz进行缓存
     * @param candidates
     */
    private void tryCacheClasses(Set<Class<?>> candidates) {
        boolean debugEnabled = LOGGER.isDebugEnabled();
        
        for (Class<?> clazz : candidates) {
            //配置修改后的自动refresh
            PropMgt propMgtAnnotation = clazz.getAnnotation(PropMgt.class);
            if(null!=propMgtAnnotation) {
                //处理class的注解(PropMgt)
                handlePropMgtAnnotation(propMgtAnnotation, clazz, debugEnabled);
                //处理get方法注解(PropMgtMethod)
                handlePropMgtMethodAnnotation(clazz, debugEnabled);
            }
            
            //配置修改后的回调
            PropMgtUpdateCallback updateCallbackAnnotation = clazz.getAnnotation(PropMgtUpdateCallback.class);
            if(null!=updateCallbackAnnotation) {
                //处理回调注解(PropMgtUpdateCallback)
                handlePropMgtUpdateCallbackAnnotation(updateCallbackAnnotation, clazz, debugEnabled);
            }
        }
    }
    
    /**
     * 
     * @param clazz
     */
    private void handlePropMgtMethodAnnotation(Class<?> clazz, boolean debugEnabled) {
        Map<Class<?>, Map<Method, String>> clazz2FieldKey = ClazzCache.getInstance().getPropMgtClazz2FieldKey();
        Map<Method, String> method2FieldKey = clazz2FieldKey.get(clazz);
        if(null==method2FieldKey) {
            method2FieldKey = new HashMap<>();
        }
        
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            PropMgtMethod propMgtMethodAnnotation = method.getAnnotation(PropMgtMethod.class);
            if(null != propMgtMethodAnnotation) {
                String key = propMgtMethodAnnotation.key();
                method2FieldKey.put(method, key);
            }
        }
        
        clazz2FieldKey.put(clazz, method2FieldKey);
    }
    
    /**
     * 处理 PropMgt annotation
     * 
     * @param propMgtAnnotation
     * @param currClazz
     * @param debugEnabled
     */
    private void handlePropMgtAnnotation(PropMgt propMgtAnnotation, Class<?> currClazz, boolean debugEnabled) {
        String host = propMgtAnnotation.host();
        String filename = propMgtAnnotation.filename();
        if(debugEnabled) {
            LOGGER.debug("Cache class, class : [" + currClazz + "], host : " + host + ", filename : " + filename);
        }
        
        if(StringUtils.isEmpty(host) || StringUtils.isEmpty(filename)) {
            throw new PropMgtException(String.format("Host or filename is empty, host : %s , filename : %s", host, filename));
        }
        
        //class进行缓存
        ClazzCache.getInstance().getHostFile2PropMgtClazz().put(new HostConfigFile(host, filename), currClazz);
    }
    
    /**
     * 处理 PropMgtUpdateCallback annotation
     * @param updateCallbackAnnotation
     * @param currClazz
     * @param debugEnabled
     */
    private void handlePropMgtUpdateCallbackAnnotation(PropMgtUpdateCallback updateCallbackAnnotation, Class<?> currClazz, boolean debugEnabled) {
        Class<?>[] classes = updateCallbackAnnotation.classes();
        if(debugEnabled) {
            LOGGER.debug("Update callback, CallbackClass : " + currClazz + ", update class : [" + StringUtils.join(classes, ",") + "]");
        }
        
        for (Class<?> tmpClass : classes) {
            Map<Class<?>, Set<Class<?>>> tmpMap = ClazzCache.getInstance().getPropMgtClass2PropCallbackClazz();
            Set<Class<?>> updateCallClasses = tmpMap.get(tmpMap);
            if(null==updateCallClasses) {
                updateCallClasses = Sets.newHashSet();
            }
            
            updateCallClasses.add(currClazz);
            tmpMap.put(tmpClass, updateCallClasses);
        }
    }
    
    /**
     * 
     * @param clazz
     * @return
     */
    private boolean isCandidateClazz(Class<?> clazz) {
        if(null==clazz || clazz.isInterface()) {
            return false;
        }
        
        PropMgt propMgtAnnotation = clazz.getAnnotation(PropMgt.class);
        PropMgtUpdateCallback propMgtUpdateCallback = clazz.getAnnotation(PropMgtUpdateCallback.class);
        if(null!=propMgtAnnotation || null!=propMgtUpdateCallback) {
            return true;
        }
        
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        this.applicationContext = arg0;
    }
}
