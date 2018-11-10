package org.loguno.processor.handlers;

import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Pair;
import org.loguno.Loguno;
import org.loguno.processor.LogunoScanner;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;


import java.lang.annotation.Annotation;


/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerParenthesesVariables<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerParenthesesVariables(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerParenthesesVariables<Loguno.TRACE, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerParenthesesVariables<Loguno.DEBUG, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerParenthesesVariables<Loguno.INFO, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerParenthesesVariables<Loguno.WARN, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerParenthesesVariables<Loguno.ERROR, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerParenthesesVariables<Loguno, LogunoScanner.VarInParentheses> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, LogunoScanner.VarInParentheses element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }


    void doRealJob(String[] value, String logMethod, LogunoScanner.VarInParentheses element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT, classContext);


        JCTree.JCBlock block =(JCTree.JCBlock) element.block();
        JCTree parentheses = element.parentheses();
        JCTree.JCVariableDecl variable = element.variable();


        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(parentheses)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.PAIR, variable.getName().toString()))
                .build()
                .create();



        //ExecutableElement methodElement = (ExecutableElement) element.getEnclosingElement();

        //MethodTree methodTree = (MethodTree) trees.getTree(element.getEnclosingElement());



        block.stats = JCTreeUtils.generateNewBody(parentheses,block, methodCall);
    }
}
