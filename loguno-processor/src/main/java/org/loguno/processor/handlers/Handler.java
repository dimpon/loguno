package org.loguno.processor.handlers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Handler {

    int order() default 1;

    Handler.RunOrder value() default Handler.RunOrder.BEFORE;

    enum RunOrder {
        BEFORE,
        AFTER
    }
}
