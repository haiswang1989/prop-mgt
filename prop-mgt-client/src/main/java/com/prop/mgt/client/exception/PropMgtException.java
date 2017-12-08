package com.prop.mgt.client.exception;

/**
 * 
 * <p>Description:</p>
 * @author hansen.wang
 * @date 2017年12月1日 上午10:18:03
 */
public class PropMgtException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;

    public PropMgtException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropMgtException(String message) {
        super(message);
    }

    public PropMgtException(Throwable cause) {
        super(cause);
    }

    public PropMgtException() {
        super();
    }
}
