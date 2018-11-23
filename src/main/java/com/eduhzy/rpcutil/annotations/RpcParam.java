package com.eduhzy.rpcutil.annotations;

import java.lang.annotation.*;

/**
 * Created by lewis ren on 2018-11-15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RpcParam {

    /**
     * 参数名
     *
     * @return
     */
    String name();

    /**
     * 描述默认为空
     *
     * @return
     */
    String description() default "";

    /**
     * 是否必填
     *
     * @return
     */
    boolean isRequired() default false;

    /**
     * 长度，默认为0的话，会根据数据类型取通用长度
     *
     * @return
     */
    int length() default 0;

    /**
     * 排序，默认为0
     *
     * @return
     */
    int sort() default 0;

    /**
     * json转换
     */
    Class<?> cls() default Object.class;
}
