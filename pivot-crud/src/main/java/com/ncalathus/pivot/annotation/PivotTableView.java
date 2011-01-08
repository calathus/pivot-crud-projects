package com.ncalathus.pivot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PivotTableView {
    String order() default ""; // csv of PivotTableViewColumn's names, A,B,C, 
}
