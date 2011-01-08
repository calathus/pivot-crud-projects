package com.ncalathus.pivot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface PivotForm {
	
    String label();
    
    String styles() default "{showFlagIcons:false, showFlagHighlight:false, padding:0}";
    
}
