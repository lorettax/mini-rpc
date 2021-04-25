package com.lorettax.rpc.provider.registry;

import com.lorettax.rpc.common.ServiceMeta;

import java.io.IOException;

/**
 * @author ljg
 */
public interface RegistryService {

    void register(ServiceMeta serviceMeta) throws Exception;

    void  unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName,int invokerHashCode) throws Exception;

    void destory() throws IOException;

}
