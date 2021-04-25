package com.lorettax.rpc.common;

import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * @author ljg
 * @param <T>
 */
@Data
public class LinkRpcFuture<T> {
    private Promise<T> promise;
    private long timeout;

    public LinkRpcFuture(Promise<T> promise,long timeout){
        this.promise = promise;
        this.timeout = timeout;
    }
}
