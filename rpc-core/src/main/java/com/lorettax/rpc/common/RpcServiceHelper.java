package com.lorettax.rpc.common;

/**
 * @author ljg
 */
public class RpcServiceHelper {

    public static String buildServiceKey(String serviceName,String serviceVersion) {
        return String.join("#",serviceName);
    }

}
