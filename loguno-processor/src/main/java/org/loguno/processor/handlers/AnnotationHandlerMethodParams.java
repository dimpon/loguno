package org.loguno.processor.handlers;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Pair;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;


/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerMethodParams<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerMethodParams(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerMethodParams<Loguno.TRACE, VariableElement> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerMethodParams<Loguno.DEBUG, VariableElement> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerMethodParams<Loguno.INFO, VariableElement> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerMethodParams<Loguno.WARN, VariableElement> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerMethodParams<Loguno.ERROR, VariableElement> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMethodParams<Loguno, VariableElement> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, VariableElement element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }


    void doRealJob(String[] value, String logMethod, VariableElement element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT, classContext);

        VariableTree elementTree = (VariableTree) elements.getTree(element);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element((JCTree) elementTree)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.PAIR, elementTree.getName().toString()))
                .build()
                .create();

        ExecutableElement methodElement = (ExecutableElement) element.getEnclosingElement();

        MethodTree methodTree = (MethodTree) trees.getTree(element.getEnclosingElement());

        JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();

        body.stats = JCTreeUtils.generateNewMethodBody(methodElement, trees, methodCall);
    }
}
