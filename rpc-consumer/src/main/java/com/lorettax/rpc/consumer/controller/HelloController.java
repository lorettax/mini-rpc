package com.lorettax.rpc.consumer.controller;

import com.lorettax.rpc.consumer.annoctation.RpcReference;
import com.lorettax.rpc.provider.facade.HelloFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class HelloController {

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @RpcReference(serviceVersion = "1.0.0",timeout = 3000)
    private HelloFacade helloFacade;

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    private String sayHello() {
        return helloFacade.hello("hello link-rpc");
    }

}
