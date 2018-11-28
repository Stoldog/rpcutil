package com.eduhzy.rpcutil.annotations;

import java.lang.annotation.*;

/**
 * Created by lewis ren on 2018-11-15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RpcMethod {
    /**
     * 接口名称
     *
     * @return
     */
    String name();

    /**
     * 接口描述,默认为接口名称
     *
     * @return
     */
    String description() default "";

    /**
     * 是否需要鉴权
     *
     * @return
     */
    boolean needAuth() default true;

    /**
     * 是否需要客户端IP
     *
     * @return
     */
    boolean needIP() default false;

    /**
     * 是否显示
     *
     * @return
     */
    boolean isShow() default true;

    /**
     * 返回值的 class
     * List<JavaBean> 应该是JavaBean.class
     * 优先级高于 returnJson
     *
     * @return
     */
    Class returnClass() default Object.class;

    /**
     * 是否是集合类型或者是数组类型
     *
     * @return
     */
    boolean collectionType() default false;

    /**
     * 返回值
     *
     * @return
     */
    String returnJson() default "{}";

}
