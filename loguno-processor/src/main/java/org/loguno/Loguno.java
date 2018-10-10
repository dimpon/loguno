package org.loguno;

import org.loguno.processor.handlers.ClassContext;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Repeatable(Loguno.$List.class)
@Retention(RetentionPolicy.SOURCE)
@Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
public @interface Loguno {

    String[] value() default "";

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface $List {
        Loguno[] value();
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(TYPE)
    @interface Slf4j {
        String value() default "LOG";
        boolean lazy() default false;
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(TYPE)
    @interface Log4j {
        String[] value() default "";
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(TYPE)
    @interface Log {
        String[] value() default "";
    }



    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(LOCAL_VARIABLE)
    @interface Catch {
        String[] value() default "";
        Class<? extends Throwable>[] exception() default {Throwable.class};

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(LOCAL_VARIABLE)
        @interface TRACE {
            String[] value() default "";
            Class<? extends Throwable>[] exception() default {Throwable.class};
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(LOCAL_VARIABLE)
        @interface DEBUG {
            String[] value() default "";
            Class<? extends Throwable>[] exception() default {Throwable.class};
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(LOCAL_VARIABLE)
        @interface INFO {
            String[] value() default "";
            Class<? extends Throwable>[] exception() default {Throwable.class};
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(LOCAL_VARIABLE)
        @interface WARN {
            String[] value() default "";
            Class<? extends Throwable>[] exception() default {Throwable.class};
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(LOCAL_VARIABLE)
        @interface ERROR {
            String[] value() default "";
            Class<? extends Throwable>[] exception() default {Throwable.class};
        }
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target(METHOD)
    @interface Return {
        String[] value() default "";


        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(METHOD)
        @interface TRACE {
            String[] value() default "";
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(METHOD)
        @interface DEBUG {
            String[] value() default "";
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(METHOD)
        @interface INFO {
            String[] value() default "";
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(METHOD)
        @interface WARN {
            String[] value() default "";
        }

        @Documented
        @Retention(RetentionPolicy.SOURCE)
        @Target(METHOD)
        @interface ERROR {
            String[] value() default "";
        }
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface TRACE {
        String[] value() default "";
    }
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface DEBUG {
        String[] value() default "";
        String string() default "";
        /*
        boolean context() default false;
        ClassContext.Logger logger() default ClassContext.Logger.Log4j;*/
    }
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface INFO {
        String[] value() default "";
    }
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface WARN {
        String[] value() default "";
    }
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    @Target({METHOD, TYPE, LOCAL_VARIABLE, PARAMETER, TYPE_PARAMETER, TYPE_USE})
    @interface ERROR {
        String[] value() default "";
    }
}



