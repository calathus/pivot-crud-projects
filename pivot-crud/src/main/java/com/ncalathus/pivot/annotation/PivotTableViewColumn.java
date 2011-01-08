package com.ncalathus.pivot.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface PivotTableViewColumn {
    String name();
    
    String headerData() default ""; // JavaScripts
    //HeaderDataRenderer headerDataRenderer = DEFAULT_HEADER_DATA_RENDERER;
    String headerDataRenderer() default ""; // JavaScripts
    int width() default 0; 
    int minimumWidth() default 0;
    int maximumWidth() default Integer.MAX_VALUE;
    boolean relative() default false;
    //Object filter = null;
    String filter() default ""; // JavaScripts
    //CellRenderer cellRenderer = DEFAULT_CELL_RENDERER;
    String cellRenderer() default ""; // JavaScripts
}
