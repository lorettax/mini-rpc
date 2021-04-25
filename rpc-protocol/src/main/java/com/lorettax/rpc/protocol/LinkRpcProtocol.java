package com.lorettax.rpc.protocol;

import lombok.Data;

import java.io.Serializable;

@Data
public class LinkRpcProtocol<T> implements Serializable {

    private MsgHeader header;
    private T body;
}
