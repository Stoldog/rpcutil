package com.eduhzy.rpcutil.core;


/**
 * 为了方便以后拓展，将扫描类抽象
 *
 * @author lewis ren
 * @date 2018-11-15
 */
public interface ApiScanner<R> {

    /**
     * 扫描方法
     *
     * @param config 相关文档生成配置
     * @param obj    被扫描类的class
     * @return
     */
    R scan(RpcConfig config, Class<?> obj);

}
