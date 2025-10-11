package com.yushijie.common.core.messagebus.idempotent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Bob.Yang
 * @version 1.0
 * @description 消息幂等性自定义注解
 * @date 2023-09-25 18:19
 */
// 注解信息会被添加到Java文档中
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MessageIdempotent {

    boolean ignore() default false;

}
