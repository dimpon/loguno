package org.loguno.processor.handlers;

import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Pair;
import org.loguno.Loguno;

import org.loguno.processor.configuration.ConfigurationKeys;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ElementKind;

import java.lang.annotation.Annotation;
import java.util.stream.Collectors;

import static org.loguno.processor.configuration.ConfigurationKeys.*;

public abstract class AnnotationHandlerMethod<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerMethod(JavacProcessingEnvironment environment) {
        super(environment);
    }


    @Handler(value = Handler.RunOrder.BEFORE, order = 1)
    public static class AnnotationHandlerBefore extends AnnotationHandlerBase<VoidAnnotation, JCTree.JCMethodDecl> {

        public AnnotationHandlerBefore(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            classContext.getMethods().addLast(element.getName().toString());
        }
    }

    @Handler(value = Handler.RunOrder.AFTER, order = 1)
    public static class AnnotationHandlerAfter extends AnnotationHandlerBase<VoidAnnotation, JCTree.JCMethodDecl> {

        public AnnotationHandlerAfter(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            classContext.getMethods().removeLast();
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMethod<Loguno, JCTree.JCMethodDecl> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerMethod<Loguno.DEBUG, JCTree.JCMethodDecl> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerMethod<Loguno.INFO, JCTree.JCMethodDecl> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerMethod<Loguno.ERROR, JCTree.JCMethodDecl> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerMethod<Loguno.TRACE, JCTree.JCMethodDecl> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerMethod<Loguno.WARN, JCTree.JCMethodDecl> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, JCTree.JCMethodDecl methodTree, ClassContext classContext) {

        if (methodTree.getBody().stats == null)
            return;

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
                .params(methodTree.getParameters().stream()
                        .map(o -> Pair.of(JCLogMethodBuilder.ParamType.PAIR, o.getName().toString()))
                        .collect(Collectors.toList()))
                .build();


        JCTree.JCStatement methodCall = builder.create();

        JCTree.JCBlock body = methodTree.getBody();

        body.stats = JCTreeUtils.generateNewBody(methodTree, body, methodCall);

    }


}
