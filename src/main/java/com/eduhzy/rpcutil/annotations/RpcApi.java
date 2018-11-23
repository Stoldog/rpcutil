package com.eduhzy.rpcutil.annotations;

import java.lang.annotation.*;

/**
 * Created by lewis ren on 2018-11-15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcApi {

    /**
     * appId,所属应用
     *
     * @return
     */
    int appId();

    /**
     * 标题
     * @return
     */
    String title();

    /**
     *
     * @return
     */
    String description() default "";

    /**
     * 模块名，默认为RpcServerContext的模块名（暂未实现）
     *
     * @return
     */
    String moduleName();

    /**
     * 服务名字,默认为RpcServerContext的服务名
     *
     * @return
     */
    String serviceName() default "";


}
