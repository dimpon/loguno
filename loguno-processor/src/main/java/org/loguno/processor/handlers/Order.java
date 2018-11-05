package org.loguno.processor.handlers;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Order {
	int value() default 0;

	RunOrder runOrder() default RunOrder.BEFORE;

	enum RunOrder {
		BEFORE,
		AFTER
	}
}
