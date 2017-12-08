package com.prop.mgt.client.zookeeper;

import java.util.List;
import org.I0Itec.zkclient.ZkClient;
import com.prop.mgt.client.common.PropMgtUtils;
import lombok.Setter;

/**
 *  
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月4日 下午4:58:35
 */
public class ZkUtils {
    
    @Setter
    private static ZkClient zkClient;
    
    /**
     * 获取指定host的指定文件的内容(配置信息)
     * @param host
     * @param propertyFilename
     */
    public static String getDate(String host, String propertyFilename) {
        String fullPath = PropMgtUtils.getFullPath(host, propertyFilename);
        
        if(zkClient.exists(fullPath)) {
            return zkClient.readData(fullPath, true);
        } else {
            return "{}";
        }
    }
    
    /**
     * 获取HOST结点下面的所有PropertyFile结点
     * @param host
     * @return
     */
    public static List<String> getChildNode(String host) {
        String fullPath = PropMgtUtils.getHostPath(host);
        return zkClient.getChildren(fullPath);
    }
}
