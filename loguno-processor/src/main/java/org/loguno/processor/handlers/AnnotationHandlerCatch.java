package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;

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

    void doRealJob(String[] value, String logMethod, JCTree.JCCatch element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .build()
                .addParam(element.getParameter().getName().toString())
                .create();

        JCTree.JCBlock body = element.getBlock();

        body.stats = body.stats.prepend(methodCall);
    }
}
