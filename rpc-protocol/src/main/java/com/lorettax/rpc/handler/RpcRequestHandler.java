package com.lorettax.rpc.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lorettax.rpc.common.LinkRpcRequest;
import com.lorettax.rpc.common.LinkRpcResponse;
import com.lorettax.rpc.common.RpcServiceHelper;
import com.lorettax.rpc.protocol.LinkRpcProtocol;
import com.lorettax.rpc.protocol.MsgHeader;
import com.lorettax.rpc.protocol.MsgStatus;
import com.lorettax.rpc.protocol.MsgType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<LinkRpcProtocol<LinkRpcRequest>> {

    private final Map<String,Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                LinkRpcProtocol<LinkRpcRequest> linkRpcRequestLinkRpcProtocol) throws Exception {
        RpcRequestProcessor.submitRequest(()->{
            LinkRpcProtocol<LinkRpcResponse> resProtocol = new LinkRpcProtocol<>();
            LinkRpcResponse response = new LinkRpcResponse();
            MsgHeader header = linkRpcRequestLinkRpcProtocol.getHeader();
            header.setMsgType((byte) MsgType.RESPONSE.getType());
            try {
                Object result = handle(linkRpcRequestLinkRpcProtocol.getBody());
                response.setData(result);

                header.setStatus((byte) MsgStatus.SUCCESS.getCode());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
            }catch (Throwable throwable){
                header.setStatus((byte) MsgStatus.FAIL.getCode());
                response.setMessage(throwable.toString());
                log.error("process request {} error",header.getRequestId(),throwable);
            }
            channelHandlerContext.writeAndFlush(resProtocol);
        });
    }



    private Object handle(LinkRpcRequest request) throws Throwable {
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(),
                request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException(
                    String.format("service not exist: %s:%s",request.getClassName(),request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParams();

        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName,parameterTypes);
        return fastClass.invoke(methodIndex,serviceBean,parameters);

    }



}
