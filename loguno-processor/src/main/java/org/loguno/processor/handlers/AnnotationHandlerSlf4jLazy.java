package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import lombok.SneakyThrows;
import org.loguno.Loguno;

import javax.lang.model.element.TypeElement;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dmitrii Ponomarev
 */
@Handler
@Order(2)
public class AnnotationHandlerSlf4jLazy extends AnnotationHandlerBase<Loguno.Slf4j, TypeElement> {


    private static final String lazyFactoryClass = "org.loguno.lazy.LazyLoggerFactorySlf4j";

    public AnnotationHandlerSlf4jLazy(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @SneakyThrows(IOException.class)
    @Override
    public void processTree(Loguno.Slf4j annotation, TypeElement typeElement, ClassContext classContext) {

        if (!classContext.isLazy())
            return;

        if (classContext.isLoggerHere(ClassContext.Logger.Slf4j))
            return;

        JavaFileObject builderFile = filer
                .createSourceFile(lazyFactoryClass);
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.print("package org.loguno.lazy;\n");
            out.print("import java.util.concurrent.atomic.AtomicReference;\n");
            out.print("import java.util.function.Function;\n");
            out.print("import org.slf4j.Logger;\n");
            out.print("import org.slf4j.LoggerFactory;\n");
            out.print("import org.slf4j.Marker;\n");
            out.print("public class LazyLoggerFactorySlf4j implements Logger {\n");
            out.print("private static final Function<Class<?>, Logger> $function = LoggerFactory::getLogger;\n");
            out.print("private final Class<?> clazz;\n");
            out.print("private final AtomicReference<Object> logger = new AtomicReference();\n");
            out.print("private Logger createLogger() { return (Logger)$function.apply(this.clazz); }\n");
            out.print("private LazyLoggerFactorySlf4j(Class<?> clazz) { this.clazz = clazz; }\n");
            out.print("public static Logger getLogger(Class<?> clazz) { return new LazyLoggerFactorySlf4j(clazz); }\n");
            out.print("private Logger getLogger() {\n");
            out.print("Object value = this.logger.get();\n");
            out.print("if (value == null) {\n");
            out.print("AtomicReference var2 = this.logger;\n");
            out.print("synchronized(this.logger) {\n");
            out.print("value = this.logger.get();\n");
            out.print("if (value == null) {\n");
            out.print("Logger actualValue = this.createLogger();\n");
            out.print("value = actualValue == null ? this.logger : actualValue;\n");
            out.print("this.logger.set(value);\n");
            out.print("}\n");
            out.print("}\n");
            out.print("}\n");
            out.print("return (Logger)((Logger)(value == this.logger ? null : value));\n");
            out.print("}\n");
            out.print("public String getName() { return this.getLogger().getName(); }\n");
            out.print("public boolean isTraceEnabled() { return this.getLogger().isTraceEnabled(); }\n");
            out.print("public void trace(String arg0) { this.getLogger().trace(arg0); }\n");
            out.print("public void trace(String arg0, Object arg1) { this.getLogger().trace(arg0, arg1); }\n");
            out.print("public void trace(String arg0, Object arg1, Object arg2) { this.getLogger().trace(arg0, arg1, arg2); }\n");
            out.print("public void trace(String arg0, Object... arg1) { this.getLogger().trace(arg0, arg1); }\n");
            out.print("public void trace(String arg0, Throwable arg1) { this.getLogger().trace(arg0, arg1); }\n");
            out.print("public boolean isTraceEnabled(Marker arg0) { return this.getLogger().isTraceEnabled(arg0); }\n");
            out.print("public void trace(Marker arg0, String arg1) { this.getLogger().trace(arg0, arg1); }\n");
            out.print("public void trace(Marker arg0, String arg1, Object arg2) { this.getLogger().trace(arg0, arg1, arg2); }\n");
            out.print("public void trace(Marker arg0, String arg1, Object arg2, Object arg3) { this.getLogger().trace(arg0, arg1, arg2, arg3); }\n");
            out.print("public void trace(Marker arg0, String arg1, Object... arg2) { this.getLogger().trace(arg0, arg1, arg2); }\n");
            out.print("public void trace(Marker arg0, String arg1, Throwable arg2) { this.getLogger().trace(arg0, arg1, arg2); }\n");
            out.print("public boolean isDebugEnabled() { return this.getLogger().isDebugEnabled(); }\n");
            out.print("public void debug(String arg0) { this.getLogger().debug(arg0); }\n");
            out.print("public void debug(String arg0, Object arg1) { this.getLogger().debug(arg0, arg1); }\n");
            out.print("public void debug(String arg0, Object arg1, Object arg2) { this.getLogger().debug(arg0, arg1, arg2); }\n");
            out.print("public void debug(String arg0, Object... arg1) { this.getLogger().debug(arg0, arg1); }\n");
            out.print("public void debug(String arg0, Throwable arg1) { this.getLogger().debug(arg0, arg1); }\n");
            out.print("public boolean isDebugEnabled(Marker arg0) { return this.getLogger().isDebugEnabled(arg0); }\n");
            out.print("public void debug(Marker arg0, String arg1) { this.getLogger().debug(arg0, arg1); }\n");
            out.print("public void debug(Marker arg0, String arg1, Object arg2) { this.getLogger().debug(arg0, arg1, arg2); }\n");
            out.print("public void debug(Marker arg0, String arg1, Object arg2, Object arg3) { this.getLogger().debug(arg0, arg1, arg2, arg3); }\n");
            out.print("public void debug(Marker arg0, String arg1, Object... arg2) { this.getLogger().debug(arg0, arg1, arg2); }\n");
            out.print("public void debug(Marker arg0, String arg1, Throwable arg2) { this.getLogger().debug(arg0, arg1, arg2); }\n");
            out.print("public boolean isInfoEnabled() { return this.getLogger().isInfoEnabled(); }\n");
            out.print("public void info(String arg0) { this.getLogger().info(arg0); }\n");
            out.print("public void info(String arg0, Object arg1) { this.getLogger().info(arg0, arg1); }\n");
            out.print("public void info(String arg0, Object arg1, Object arg2) { this.getLogger().info(arg0, arg1, arg2); }\n");
            out.print("public void info(String arg0, Object... arg1) { this.getLogger().info(arg0, arg1); }\n");
            out.print("public void info(String arg0, Throwable arg1) { this.getLogger().info(arg0, arg1); }\n");
            out.print("public boolean isInfoEnabled(Marker arg0) { return this.getLogger().isInfoEnabled(arg0); }\n");
            out.print("public void info(Marker arg0, String arg1) { this.getLogger().info(arg0, arg1); }\n");
            out.print("public void info(Marker arg0, String arg1, Object arg2) { this.getLogger().info(arg0, arg1, arg2); }\n");
            out.print("public void info(Marker arg0, String arg1, Object arg2, Object arg3) { this.getLogger().info(arg0, arg1, arg2, arg3); }\n");
            out.print("public void info(Marker arg0, String arg1, Object... arg2) { this.getLogger().info(arg0, arg1, arg2); }\n");
            out.print("public void info(Marker arg0, String arg1, Throwable arg2) { this.getLogger().info(arg0, arg1, arg2); }\n");
            out.print("public boolean isWarnEnabled() { return this.getLogger().isWarnEnabled(); }\n");
            out.print("public void warn(String arg0) { this.getLogger().warn(arg0); }\n");
            out.print("public void warn(String arg0, Object arg1) { this.getLogger().warn(arg0, arg1); }\n");
            out.print("public void warn(String arg0, Object... arg1) { this.getLogger().warn(arg0, arg1); }\n");
            out.print("public void warn(String arg0, Object arg1, Object arg2) { this.getLogger().warn(arg0, arg1, arg2); }\n");
            out.print("public void warn(String arg0, Throwable arg1) { this.getLogger().warn(arg0, arg1); }\n");
            out.print("public boolean isWarnEnabled(Marker arg0) { return this.getLogger().isWarnEnabled(arg0); }\n");
            out.print("public void warn(Marker arg0, String arg1) { this.getLogger().warn(arg0, arg1); }\n");
            out.print("public void warn(Marker arg0, String arg1, Object arg2) { this.getLogger().warn(arg0, arg1, arg2); }\n");
            out.print("public void warn(Marker arg0, String arg1, Object arg2, Object arg3) { this.getLogger().warn(arg0, arg1, arg2, arg3); }\n");
            out.print("public void warn(Marker arg0, String arg1, Object... arg2) { this.getLogger().warn(arg0, arg1, arg2); }\n");
            out.print("public void warn(Marker arg0, String arg1, Throwable arg2) { this.getLogger().warn(arg0, arg1, arg2); }\n");
            out.print("public boolean isErrorEnabled() { return this.getLogger().isErrorEnabled(); }\n");
            out.print("public void error(String arg0) { this.getLogger().error(arg0); }\n");
            out.print("public void error(String arg0, Object arg1) { this.getLogger().error(arg0, arg1); }\n");
            out.print("public void error(String arg0, Object arg1, Object arg2) { this.getLogger().error(arg0, arg1, arg2); }\n");
            out.print("public void error(String arg0, Object... arg1) { this.getLogger().error(arg0, arg1); }\n");
            out.print("public void error(String arg0, Throwable arg1) { this.getLogger().error(arg0, arg1); }\n");
            out.print("public boolean isErrorEnabled(Marker arg0) { return this.getLogger().isErrorEnabled(arg0); }\n");
            out.print("public void error(Marker arg0, String arg1) { this.getLogger().error(arg0, arg1); }\n");
            out.print("public void error(Marker arg0, String arg1, Object arg2) { this.getLogger().error(arg0, arg1, arg2); }\n");
            out.print("public void error(Marker arg0, String arg1, Object arg2, Object arg3) { this.getLogger().error(arg0, arg1, arg2, arg3); }\n");
            out.print("public void error(Marker arg0, String arg1, Object... arg2) { this.getLogger().error(arg0, arg1, arg2); }\n");
            out.print("public void error(Marker arg0, String arg1, Throwable arg2) { this.getLogger().error(arg0, arg1, arg2); }\n");
            out.print("}");
        }

        classContext.generated(ClassContext.Logger.Slf4j);
    }
}
