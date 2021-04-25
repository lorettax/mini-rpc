package com.lorettax.rpc.codec;

import com.lorettax.rpc.protocol.LinkRpcProtocol;
import com.lorettax.rpc.protocol.MsgHeader;
import com.lorettax.rpc.serialization.RpcSerialization;
import com.lorettax.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class LinkRpcEncoder extends MessageToByteEncoder<LinkRpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          LinkRpcProtocol<Object> objectLinkRpcProtocol, ByteBuf byteBuf) throws Exception {
        MsgHeader header = objectLinkRpcProtocol.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getVersion());
        byteBuf.writeByte(header.getSerialization());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeByte((int) header.getRequestId());

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        byte[] data = rpcSerialization.serialize(objectLinkRpcProtocol.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
    }
}
