package com.lorettax.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ljg
 */
@Data
public class LinkRpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] parameterTypes;

}
