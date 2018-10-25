package org.loguno;

import org.loguno.processor.handlers.Frameworks;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Repeatable(Loguno.$List.class)
@Retention(RetentionPolicy.SOURCE)
@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
public @interface Loguno {

	String[] value() default "";

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface $List {
		Loguno[] value();
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target(TYPE)
	@interface Logger {
		Frameworks value() default Frameworks.NONE;
		String name() default "LOG";
		boolean lazy() default false;
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface TRACE {
		String[] value() default "";
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface DEBUG {
		String[] value() default "";
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface INFO {
		String[] value() default "";
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface WARN {
		String[] value() default "";
	}

	@Documented
	@Retention(RetentionPolicy.SOURCE)
	@Target({ METHOD, TYPE, LOCAL_VARIABLE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
	@interface ERROR {
		String[] value() default "";
	}
}
