package com.lorettax.rpc.consumer;

import com.lorettax.rpc.common.LinkRpcFuture;
import com.lorettax.rpc.common.LinkRpcRequest;
import com.lorettax.rpc.common.LinkRpcRequestHolder;
import com.lorettax.rpc.common.LinkRpcResponse;
import com.lorettax.rpc.protocol.LinkRpcProtocol;
import com.lorettax.rpc.protocol.MsgHeader;
import com.lorettax.rpc.protocol.MsgType;
import com.lorettax.rpc.protocol.ProtocolConstants;
import com.lorettax.rpc.provider.registry.RegistryService;
import com.lorettax.rpc.serialization.SerializationTypeEnum;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RpcInvokerProxy implements InvocationHandler {

    private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;

    public RpcInvokerProxy(String serviceVersion,long timeout,RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LinkRpcProtocol<LinkRpcRequest> protocol = new LinkRpcProtocol<>();
        MsgHeader header = new MsgHeader();
        long requestId = LinkRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setVersion(ProtocolConstants.VERSION);
        header.setRequestId(requestId);
        header.setSerialization((byte) SerializationTypeEnum.HESSIAN.getType());
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        LinkRpcRequest request = new LinkRpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        protocol.setBody(request);

        RpcConsumer rpcConsumer = new RpcConsumer();
        LinkRpcFuture<LinkRpcResponse> future = new LinkRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()),timeout);
        LinkRpcRequestHolder.REQUEST_MAP.put(requestId,future);
        rpcConsumer.sendRequest(protocol,this.registryService);

        // TODO hold reqeust by ThreadLocal

        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
