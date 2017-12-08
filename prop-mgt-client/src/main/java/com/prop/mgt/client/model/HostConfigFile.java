package com.prop.mgt.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HostConfigFile {
    
    //host
    private String host;
    
    //配置文件
    private String fileName;
}
