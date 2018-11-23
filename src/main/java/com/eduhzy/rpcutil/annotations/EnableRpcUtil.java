package com.eduhzy.rpcutil.annotations;

import com.eduhzy.rpcutil.core.RpcUtilConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Created by lewis ren on 2018-11-15.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(RpcUtilConfiguration.class)
public @interface EnableRpcUtil {
    @AliasFor("path")
    String value() default "";
    @AliasFor("value")
    String path() default "";
}
