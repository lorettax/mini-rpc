package com.lorettax.rpc.provider.facade;

import com.lorettax.rpc.codec.LinkRpcDecoder;
import com.lorettax.rpc.codec.LinkRpcEncoder;
import com.lorettax.rpc.common.LinkRpcRequestHolder;
import com.lorettax.rpc.common.RpcServiceHelper;
import com.lorettax.rpc.common.ServiceMeta;
import com.lorettax.rpc.handler.RpcRequestHandler;
import com.lorettax.rpc.provider.annotation.RpcService;
import com.lorettax.rpc.provider.registry.RegistryService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private String serverAddress;
    private final int serverPort;
    private final RegistryService serviceRegistry;

    private final Map<String,Object> rpcServiceMap = new HashMap<>();

    public RpcProvider(int serverPort,RegistryService serviceRegistry) {
        this.serverPort = serverPort;
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        
        new Thread(()->{
            try {
                startRpcServer();
            }catch (Exception e) {
                log.error("start rpc server error.",e);
            }
        }).start();
    }

    private void startRpcServer() throws UnknownHostException, InterruptedException {
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    socketChannel.pipeline()
                                            .addLast(new LinkRpcEncoder())
                                            .addLast(new LinkRpcDecoder())
                                            .addLast(new RpcRequestHandler(rpcServiceMap));
                                }
                            }).childOption(ChannelOption.SO_KEEPALIVE,true);
            ChannelFuture channelFuture =bootstrap.bind(this.serverAddress,this.serverPort).sync();
            log.info("server addr {} started on port {}",this.serverAddress,this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();

            try {
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(serverAddress);
                serviceMeta.setServicePort(serverPort);
                serviceMeta.setServiceName(serviceName);
                serviceMeta.setServiceVersion(serviceVersion);

                serviceRegistry.register(serviceMeta);
                rpcServiceMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(),
                        serviceMeta.getServiceVersion()),bean);
            } catch (Exception e) {
                log.error("failed to register service {}#{}",serviceName,serviceVersion,e);
            }
        }
        return bean;
    }
}
