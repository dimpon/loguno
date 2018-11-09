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


import javax.lang.model.element.ExecutableElement;

import java.lang.annotation.Annotation;


/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerMethodParams<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerMethodParams(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerMethodParams<Loguno.TRACE, JCTree.JCVariableDecl> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerMethodParams<Loguno.DEBUG, JCTree.JCVariableDecl> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerMethodParams<Loguno.INFO, JCTree.JCVariableDecl> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerMethodParams<Loguno.WARN, JCTree.JCVariableDecl> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerMethodParams<Loguno.ERROR, JCTree.JCVariableDecl> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMethodParams<Loguno, JCTree.JCVariableDecl> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }


    void doRealJob(String[] value, String logMethod, JCTree.JCVariableDecl element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT, classContext);

        //VariableTree elementTree = (VariableTree) elements.getTree(element);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.PAIR, element.getName().toString()))
                .build()
                .create();


        MethodTree method = classContext.getMethodsBlocks().getLast();

        //ExecutableElement methodElement = (ExecutableElement) element.getEnclosingElement();

        //MethodTree methodTree = (MethodTree) trees.getTree(element.getEnclosingElement());

        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();

        body.stats = JCTreeUtils.generateNewMethodBody(method, methodCall);
    }
}
