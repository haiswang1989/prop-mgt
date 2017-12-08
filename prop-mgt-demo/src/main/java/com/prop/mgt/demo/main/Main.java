package com.prop.mgt.demo.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.prop.mgt.client.cache.PropertyHolder;
import com.prop.mgt.demo.config.Config;

@ImportResource("classpath:applicationContext.xml")
@Component
public class Main {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        Main main = context.getBean(Main.class);
        Config config = context.getBean(Config.class);
        System.out.println("values : " + config.getZkString());
        
        String value = PropertyHolder.getProperty("www.ebay.com.cn", "zk.properties", "zookeeper.connection");
        System.out.println("values : " + value);
        
        while(true) {
            System.out.println("values : " + config.getZkString());
            try {
                Thread.currentThread().sleep(5000L);
            } catch (InterruptedException e) {
            }
        }
    }
}
