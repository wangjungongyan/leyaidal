package com.leya.idal.annotation;

import com.leya.idal.enums.ActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

    ActionType action() default ActionType.QUERY_LIST;

    boolean foreWrite() default false;

}
