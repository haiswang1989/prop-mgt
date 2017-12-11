package com.prop.mgt.client.zookeeper.listener;

import java.util.List;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * /propmgt目录监听
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月11日 下午4:31:37
 */
public class HostAddListener extends AbstractChildAddListener {
    
    public static final Logger LOGGER = LoggerFactory.getLogger(HostAddListener.class);
    
    private ZkClient zkClient;
    
    public HostAddListener(ZkClient zkClientArg) {
        this.zkClient = zkClientArg;
    }
    
    @Override
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
        for (String child : currentChilds) {
            String fullPath = getFullPath(parentPath, child);
            LOGGER.info("Subscribe child changes , Path : {}", fullPath);
            //这边只是做subscribe,就算重复subscribe也没有问题
            zkClient.subscribeChildChanges(fullPath, new FileAddListener(zkClient));
        }
    }
}
