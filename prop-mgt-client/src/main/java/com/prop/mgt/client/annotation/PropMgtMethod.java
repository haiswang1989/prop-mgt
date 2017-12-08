package com.prop.mgt.client.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 修饰Field的setxxx方法
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月1日 下午6:00:30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropMgtMethod {
    
    String key();
}
