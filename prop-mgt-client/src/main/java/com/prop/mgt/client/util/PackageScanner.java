package com.prop.mgt.client.util;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;
import com.google.common.collect.Sets;

/**
 * package 扫描器
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年11月30日 下午5:32:52
 */
public class PackageScanner {
    
    private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
    private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory(RESOURCE_PATTERN_RESOLVER);
    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
    public static final Logger LOGGER = LoggerFactory.getLogger(PackageScanner.class);
    
    /**
     * 同事扫描多个目录
     * @param inputPackages
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<Class<?>> scan(Set<String> inputPackages) {
        if(CollectionUtils.isEmpty(inputPackages)) {
            LOGGER.warn("Input packages is empty.");
            return Collections.EMPTY_SET;
        }
        
        Set<Class<?>> classes = Sets.newHashSet();
        Set<Class<?>> tmpSet = null;
        for (String inputPackage : inputPackages) {
            tmpSet = scan(inputPackage);
            classes.addAll(tmpSet);
        }
        
        return classes;
    }
    
    /**
     * 扫描 Package
     * @param inputPackage
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Set<Class<?>> scan(String inputPackage) {
        if(StringUtils.isEmpty(inputPackage)) {
            LOGGER.warn("Emtry input package path");
            return Collections.EMPTY_SET;
        }
        
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(inputPackage)) + "/" + DEFAULT_RESOURCE_PATTERN;
        
        Resource[] resources = null;
        try {
            resources = RESOURCE_PATTERN_RESOLVER.getResources(packageSearchPath);
        } catch (IOException e) {
            LOGGER.warn(String.format("Scan package fail, package : {}", inputPackage), e);
            return Collections.EMPTY_SET;
        }
        
        if(null!=resources && 0!=resources.length) {
            Set<Class<?>> classes = Sets.newHashSetWithExpectedSize(resources.length);
            for (Resource resource : resources) {
                Class<?> clazz = null;
                try {
                    clazz = loadClass(resource);
                    if(null!=clazz) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    LOGGER.warn(String.format("load target class fail, resource : %s", resources.toString()), e);
                }
            }
            
            return classes;
        }
        
        return Collections.EMPTY_SET;
    }
    
    /**
     * load class
     * @param resource
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private static Class<?> loadClass(Resource resource) throws IOException, ClassNotFoundException {
        if(resource.isReadable()) {
            MetadataReader metadataReader = METADATA_READER_FACTORY.getMetadataReader(resource);
            if(metadataReader != null) {
                String className = metadataReader.getClassMetadata().getClassName();
                return Class.forName(className);
            } else {
                LOGGER.warn("Resource metadata reader is null, Resource : {}", resource);
            }
        } else {
            LOGGER.warn("Resource is not readable, Resource : {}", resource);
        }
        
        return null;
    }
}
