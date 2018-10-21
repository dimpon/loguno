package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Pair;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;
import java.util.NoSuchElementException;

public abstract class AnnotationHandlerCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerCatch(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerCatch<Loguno, JCTree.JCCatch> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCTree.JCCatch element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerInfo extends AnnotationHandlerCatch<Loguno.INFO, JCTree.JCCatch> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, JCTree.JCCatch element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerTrace extends AnnotationHandlerCatch<Loguno.TRACE, JCTree.JCCatch> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, JCTree.JCCatch element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerDebug extends AnnotationHandlerCatch<Loguno.DEBUG, JCTree.JCCatch> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, JCTree.JCCatch element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerWarn extends AnnotationHandlerCatch<Loguno.WARN, JCTree.JCCatch> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, JCTree.JCCatch element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerError extends AnnotationHandlerCatch<Loguno.ERROR, JCTree.JCCatch> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, JCTree.JCCatch element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, JCTree.JCCatch element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable  = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.VAR,element.getParameter().getName().toString()))
                .build()
                .create();

        JCTree.JCBlock body = element.getBlock();

        body.stats = body.stats.prepend(methodCall);
    }
}
