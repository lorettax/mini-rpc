package com.lorettax.rpc.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ljg
 */
@Data
public class LinkRpcResponse implements Serializable {
    private Object data;
    private String message;
}
