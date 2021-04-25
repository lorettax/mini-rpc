package com.lorettax.rpc.codec;

import com.lorettax.rpc.common.LinkRpcRequest;
import com.lorettax.rpc.common.LinkRpcResponse;
import com.lorettax.rpc.protocol.LinkRpcProtocol;
import com.lorettax.rpc.protocol.MsgHeader;
import com.lorettax.rpc.protocol.MsgType;
import com.lorettax.rpc.protocol.ProtocolConstants;
import com.lorettax.rpc.serialization.RpcSerialization;
import com.lorettax.rpc.serialization.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class LinkRpcDecoder extends ByteToMessageDecoder {



    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }
        in.markReaderIndex();

        short magic = in.readShort();
        if(magic != ProtocolConstants.MAGIC) {
            throw new  IllegalArgumentException("magic number is illegal,"+ magic);
        }

        byte version = in.readByte();
        byte serializeType = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();

        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        MsgType msgTypeEnum = MsgType.findByType(msgType);
        if (msgTypeEnum == null) {
            return;
        }

        MsgHeader header = new MsgHeader();
        header.setMagic(magic);
        header.setVersion(version);
        header.setSerialization(serializeType);
        header.setStatus(status);
        header.setRequestId(requestId);
        header.setMsgType(msgType);
        header.setMsgLen(dataLength);

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(serializeType);
        switch (msgTypeEnum) {
            case REQUEST:
                LinkRpcRequest request = rpcSerialization.deserialize(data,LinkRpcRequest.class);
                if (request != null) {
                    LinkRpcProtocol<LinkRpcRequest> protocol = new LinkRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                LinkRpcResponse response = rpcSerialization.deserialize(data,LinkRpcResponse.class);
                if (response != null) {
                    LinkRpcProtocol<LinkRpcResponse> protocol = new LinkRpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
            case HEARTBEAT:
                //TODO
                break;
        }

    }
}
