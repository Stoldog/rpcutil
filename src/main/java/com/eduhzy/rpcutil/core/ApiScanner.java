package com.eduhzy.rpcutil.core;


/**
 * 为了方便以后拓展，将扫描类抽象
 * Created by lewis ren on 2018-11-15.
 */
public interface ApiScanner<R> {

    R scan(RpcConfig config, Class<?> obj);

}
