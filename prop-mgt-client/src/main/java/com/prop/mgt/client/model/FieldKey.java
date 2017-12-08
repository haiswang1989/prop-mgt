package com.prop.mgt.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 类的Field对象Model
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月4日 上午10:26:16
 */
@Data
@AllArgsConstructor
public class FieldKey {
    
    //该Field对应的Key
    private String key;
    
    //该Field的名称
    private String associateField;
}
