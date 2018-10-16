package org.loguno.processor.handlers;

import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;

import org.loguno.processor.configuration.ConfigurationKeys;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.loguno.processor.configuration.ConfigurationKeys.*;
import static org.loguno.processor.utils.JCTreeUtils.*;

public abstract class AnnotationHandlerMethod<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerMethod(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMethod<Loguno, ExecutableElement> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, ExecutableElement element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerDebug extends AnnotationHandlerMethod<Loguno.DEBUG, ExecutableElement> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, ExecutableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerInfo extends AnnotationHandlerMethod<Loguno.INFO, ExecutableElement> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, ExecutableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerError extends AnnotationHandlerMethod<Loguno.ERROR, ExecutableElement> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, ExecutableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerTrace extends AnnotationHandlerMethod<Loguno.TRACE, ExecutableElement> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, ExecutableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerWarn extends AnnotationHandlerMethod<Loguno.WARN, ExecutableElement> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, ExecutableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, ExecutableElement element, ClassContext classContext) {

        MethodTree methodTree = trees.getTree(element);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        String message = JCTreeUtils.message(value, METHOD_MESSAGE_PATTERN_DEFAULT, classContext);

        final String repeatPart = JCTreeUtils.getRepeatPart(message);


        if (!repeatPart.isEmpty()) {
            String paramsStr = methodTree.getParameters().stream().map(o -> repeatPart).collect(Collectors.joining(","));
            message = message.replaceAll(JCTreeUtils.REPEAT_PATTERN, paramsStr);
        }

        JCLogMethodBuilder builder = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element((JCTree) methodTree)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .build();


        methodTree.getParameters().forEach(o -> {
            builder.addParamPair(o.getName().toString());
        });


        JCTree.JCStatement methodCall = builder.create();

        JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();

        body.stats = JCTreeUtils.generateNewMethodBody(element, trees, methodCall);


    }


}
