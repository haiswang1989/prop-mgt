package com.prop.mgt.client.zookeeper.listener;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;

import com.prop.mgt.client.cache.ClazzCache;

/**
 * /propmgt/{host} 目录的监听
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月11日 下午4:46:05
 */
public class FileAddListener extends AbstractChildAddListener {
    
    private ZkClient zkClient;
    
    public FileAddListener(ZkClient zkClientArg) {
        this.zkClient = zkClientArg;
    }
    
    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        String host = parentPath.substring(parentPath.lastIndexOf("/") + 1);
        for (String child : currentChilds) {
            
        }
    }
}
