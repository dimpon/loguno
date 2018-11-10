package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
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

public abstract class AnnotationHandlerBlockVariable<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerBlockVariable(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerBlockVariable<Loguno.WARN, LogunoScanner.VarInBlock> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerBlockVariable<Loguno.ERROR, LogunoScanner.VarInBlock> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerBlockVariable<Loguno.DEBUG, LogunoScanner.VarInBlock> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    /////////////////
    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerBlockVariable<Loguno.TRACE, LogunoScanner.VarInBlock> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerBlockVariable<Loguno.INFO, LogunoScanner.VarInBlock> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerBlockVariable<Loguno,LogunoScanner.VarInBlock> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, LogunoScanner.VarInBlock element, ClassContext classContext) {
            String logMethod = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), logMethod, element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, LogunoScanner.VarInBlock element, ClassContext classContext) {

        JCTree.JCBlock block = element.block();
        JCTree.JCVariableDecl variable = element.variable();

        String message = JCTreeUtils.message(value, ConfigurationKeys.LOCVAR_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(block)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.PAIR, variable.name.toString()))
                .build()
                .create();



        ListBuffer<JCTree.JCStatement> li = new ListBuffer<>();

        block.stats.forEach(st -> {
            li.append(st);
            if (st == variable)
                li.append(methodCall);
        });

        block.stats = li.toList();

    }
}
