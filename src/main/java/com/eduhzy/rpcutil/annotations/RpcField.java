package com.eduhzy.rpcutil.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 参数 注解
 *
 * @author zhongHG
 * @date 2018-11-30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface RpcField {

    /**
     * 参数描述
     *
     * @return
     */
    String description() default "";

    /**
     * 参数类型
     *
     * @return
     */
    Class paramClass() default Object.class;

    /**
     * 是否是集合类型
     *
     * @return
     */
    boolean collectionType() default false;
}
