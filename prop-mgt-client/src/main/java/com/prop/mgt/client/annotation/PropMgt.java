package com.prop.mgt.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式配置文件,用于标注class的
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年11月30日 下午2:27:48
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropMgt {
    
    /**
     * 该APP所在的HOST
     * @return
     */
    String host();
    
    /**
     * 该Class依赖的配置文件名称
     * @return
     */
    String filename();
}
