package com.prop.mgt.client.zookeeper.listener;

import org.I0Itec.zkclient.IZkChildListener;

import com.prop.mgt.client.common.PropMgtConsts;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月11日 下午5:09:53
 */
public abstract class AbstractChildAddListener implements IZkChildListener {
    
    protected static String getFullPath(String parentPath, String child) {
        StringBuilder fullPath = new StringBuilder();
        fullPath.append(parentPath).append(PropMgtConsts.PROP_MGT_ZK_SEPARATOR).append(child);
        return fullPath.toString();
    }
}
