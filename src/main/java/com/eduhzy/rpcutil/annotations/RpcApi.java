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
     * 标题
     *
     * @return
     */
    String title();

    /**
     * 模块名，默认为RpcServerContext的模块名（暂未实现）
     *
     * @return
     */
    String moduleName();

    /**
     * appId,所属应用(已废弃,统一使用RpcConfig中的appId)
     *
     * @return
     */
    @Deprecated
    int appId() default 0;

    /**
     * @return
     */
    String description() default "";

    /**
     * 服务名字,默认为RpcServerContext的服务名
     *
     * @return
     */
    String serviceName() default "";


}
