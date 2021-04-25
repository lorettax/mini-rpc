package com.lorettax.rpc.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ljg
 */
public class LinkRpcRequestHolder {

    public final static AtomicLong REQUEST_ID_GEN = new AtomicLong(0);

    public static final Map<Long,LinkRpcFuture<LinkRpcResponse>> REQUEST_MAP = new ConcurrentHashMap<>();
}
