package com.lorettax.rpc.handler;

import com.lorettax.rpc.common.LinkRpcFuture;
import com.lorettax.rpc.common.LinkRpcRequestHolder;
import com.lorettax.rpc.common.LinkRpcResponse;
import com.lorettax.rpc.protocol.LinkRpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<LinkRpcProtocol<LinkRpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LinkRpcProtocol<LinkRpcResponse> linkRpcResponseLinkRpcProtocol) throws Exception {
        long  reqeustId = linkRpcResponseLinkRpcProtocol.getHeader().getRequestId();
        LinkRpcFuture<LinkRpcResponse> future = LinkRpcRequestHolder.REQUEST_MAP.remove(reqeustId);
        future.getPromise().setSuccess(linkRpcResponseLinkRpcProtocol.getBody());
    }
}
