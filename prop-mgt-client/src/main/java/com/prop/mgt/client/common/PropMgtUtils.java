package com.prop.mgt.client.common;

import com.prop.mgt.client.model.HostConfigFile;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月6日 下午5:21:41
 */
public class PropMgtUtils {
    
    /**
     * /propmgt/{host}
     * @param host
     * @return
     */
    public static String getHostPath(String host) {
        StringBuffer hostPath = new StringBuffer();
        hostPath.append(PropMgtConsts.PROP_MGT_BASE_PATH).append(host);
        return hostPath.toString();
    }
    
    /**
     * 
     * @param host
     * @param propertyFilename
     * @return
     */
    public static String getFullPath(String host, String propertyFilename) {
        StringBuilder fullPath = new StringBuilder();
        fullPath.append(PropMgtConsts.PROP_MGT_BASE_PATH).append(host)
                .append(PropMgtConsts.PROP_MGT_ZK_SEPARATOR).append(propertyFilename);
        return fullPath.toString();
    }
    
    /**
     * /propmgt/{host}/{filename}
     * @param fullPath
     * @return
     */
    public static HostConfigFile getHostConfigFile(String fullPath) {
        String[] strs = fullPath.split("/");
        int length = strs.length;
        return new HostConfigFile(strs[length-2], strs[length-1]);
    }
}
