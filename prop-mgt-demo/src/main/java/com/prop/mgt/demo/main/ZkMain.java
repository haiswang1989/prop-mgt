package com.prop.mgt.demo.main;

import org.I0Itec.zkclient.ZkClient;

public class ZkMain {

    public static void main(String[] args) {
        
        String str = "/aaa/bbb";
        System.out.println(str.substring(str.lastIndexOf("/") + 1));
        
        
//        String zkString = "10.199.188.79:2181,10.199.187.101:2181,10.199.187.102:2181";
//        ZkClient zkClient = new ZkClient(zkString);
//        String data = "{\"zookeeper.connection\":\"10.199.188.79:2181,10.199.187.101:2181,10.199.187.102:2181\"}";
//        
//        zkClient.create("/propmgt", "", CreateMode.PERSISTENT);
//        zkClient.create("/propmgt/www.ebay.com.cn", "", CreateMode.PERSISTENT);
//        zkClient.create("/propmgt/www.ebay.com.cn/zk.properties", data, CreateMode.PERSISTENT);
//        System.out.println("over...");
        
        
        
//        String str = "/propmgt/www.ebay.com.cn/zk.properties";
//        String[] arrgs = str.split("/");
//        System.out.println(arrgs.length);
        
//        data = "{\"zookeeper.connection\":\"xxx\"}";
//        zkClient.writeData("/propmgt/www.ebay.com.cn/zk.properties", data);
        
        
    }

}
