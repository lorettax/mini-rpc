package com.lorettax.rpc.common;

import lombok.Data;

/**
 * @author ljg
 */
@Data
public class ServiceMeta {

    private String serviceName;

    private String serviceVersion;

    private String ServiceAddr;

    private int servicePort;

}
