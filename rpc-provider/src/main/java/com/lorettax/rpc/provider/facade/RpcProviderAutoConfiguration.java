package com.lorettax.rpc.provider.facade;

import com.lorettax.rpc.common.RpcProperties;
import com.lorettax.rpc.provider.registry.RegistryFactory;
import com.lorettax.rpc.provider.registry.RegistryService;
import com.lorettax.rpc.provider.registry.RegistryType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider init() throws Exception {
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService serviceRegistry = RegistryFactory.getInstance(rpcProperties.getRegistryAddr(),type);
        return new RpcProvider(rpcProperties.getServicePort(),serviceRegistry);
    }

}
