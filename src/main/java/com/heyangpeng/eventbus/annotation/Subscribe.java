package com.heyangpeng.eventbus.annotation;

import com.heyangpeng.eventbus.common.ThreadType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    ThreadType threadType() default ThreadType.SYNC;

}
