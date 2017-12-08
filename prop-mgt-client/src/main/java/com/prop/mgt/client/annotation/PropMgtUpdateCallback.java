package com.prop.mgt.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 配置文件修改回调,用于标注Class
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年11月30日 下午3:08:54
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropMgtUpdateCallback {
    
    /**
     * 关注哪些class的配置文件的变化
     * @return
     */
    Class<?>[] classes();
}
