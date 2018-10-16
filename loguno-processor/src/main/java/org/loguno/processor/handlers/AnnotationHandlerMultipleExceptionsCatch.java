package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.Name;
import java.lang.annotation.Annotation;

public abstract class AnnotationHandlerMultipleExceptionsCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    public AnnotationHandlerMultipleExceptionsCatch(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerDebug extends AnnotationHandlerMultipleExceptionsCatch<Loguno.DEBUG, ExceptionTO> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, ExceptionTO element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMultipleExceptionsCatch<Loguno, ExceptionTO> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, ExceptionTO element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, ExceptionTO to, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(to.element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .build()
                .addParam(to.e.toString())
                .create();


        JCTree.JCIdent except = factory.Ident(elements.getName(to.e));

        JCTree.JCIf anIf = factory.at(to.element.pos())
                .If(factory.Parens(factory.TypeTest(except, to.exceptionClass)), methodCall, null);

        to.body.stats = to.body.stats.prepend(anIf);

    }

    @AllArgsConstructor(staticName = "of")
    public static class ExceptionTO{
        private final Name e;
        private final JCTree.JCBlock body;
        private final JCTree.JCExpression exceptionClass;
        private final JCTree element;
    }
}
