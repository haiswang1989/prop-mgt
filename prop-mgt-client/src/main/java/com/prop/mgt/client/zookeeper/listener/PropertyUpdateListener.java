package com.prop.mgt.client.zookeeper.listener;

import org.I0Itec.zkclient.IZkDataListener;

import com.prop.mgt.client.cache.ClazzCache;
import com.prop.mgt.client.common.PropMgtUtils;
import com.prop.mgt.client.model.HostConfigFile;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月6日 下午5:12:53
 */
public class PropertyUpdateListener implements IZkDataListener {

    @Override
    public void handleDataChange(String dataPath, Object data) throws Exception {
        HostConfigFile hostConfigFile = PropMgtUtils.getHostConfigFile(dataPath);
        ClazzCache.getInstance().reflash(hostConfigFile, data);
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
    }
}
