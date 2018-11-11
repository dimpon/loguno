package org.loguno.processor.handlers;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.CatchTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Pair;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerPipedExceptionsCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    public AnnotationHandlerPipedExceptionsCatch(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    public static class AnnotationHandlerPipedExceptions extends AnnotationHandlerPipedExceptionsCatch<VoidAnnotation, PipedExceptionsHolder> {

        public AnnotationHandlerPipedExceptions(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, PipedExceptionsHolder pipedExceptionsHolder, ClassContext classContext) {

            final String e = pipedExceptionsHolder.varName;
            final CatchTree catchTree = pipedExceptionsHolder.element;
            final JCTree.JCBlock block = (JCTree.JCBlock) catchTree.getBlock();

            pipedExceptionsHolder.$exceptions.forEach((key, annotationsTree) -> {

                if (annotationsTree == null || annotationsTree.isEmpty())
                    return;

                final JCStatementHolder holder = JCStatementHolder.of()
                        .element(block)
                        .exceptionName(e);

                Stream<? extends Annotation> annotations = retriever.getTreeAnnotations(annotationsTree);

                annotations.forEach(ann ->
                        HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), holder)
                                .forEach(h -> h.process(ann, holder, classContext)));


                JCTree.JCStatement body;

                if (holder.getLogMethods().size() == 0) {
                    body = null;
                } else if (holder.$logMethods.size() > 1) {
                    body = factory.at(block.pos()).Block(0, holder.getLogMethods().toList());
                } else {
                    body = holder.getLogMethods().first();
                }

                JCTree.JCIdent except = factory.Ident(elements.getName(e));

                JCTree.JCIf anIf = factory.at(block.pos())
                        .If(factory.Parens(factory.TypeTest(except, key)), body, null);

                block.stats = block.stats.prepend(anIf);

            });
        }
    }

    @Handler
    public static class AnnotationHandlerDebug extends AnnotationHandlerPipedExceptionsCatch<Loguno.DEBUG, JCStatementHolder> {

        public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.DEBUG annotation, JCStatementHolder element, ClassContext classContext) {
            doRealJob(annotation.value(), "debug", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerInfo extends AnnotationHandlerPipedExceptionsCatch<Loguno.INFO, JCStatementHolder> {

        public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.INFO annotation, JCStatementHolder element, ClassContext classContext) {
            doRealJob(annotation.value(), "info", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerTrace extends AnnotationHandlerPipedExceptionsCatch<Loguno.TRACE, JCStatementHolder> {

        public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.TRACE annotation, JCStatementHolder element, ClassContext classContext) {
            doRealJob(annotation.value(), "trace", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerWarn extends AnnotationHandlerPipedExceptionsCatch<Loguno.WARN, JCStatementHolder> {

        public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.WARN annotation, JCStatementHolder element, ClassContext classContext) {
            doRealJob(annotation.value(), "warn", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerError extends AnnotationHandlerPipedExceptionsCatch<Loguno.ERROR, JCStatementHolder> {

        public AnnotationHandlerError(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno.ERROR annotation, JCStatementHolder element, ClassContext classContext) {
            doRealJob(annotation.value(), "error", element, classContext);
        }
    }

    @Handler
    public static class AnnotationHandlerLoguno extends AnnotationHandlerPipedExceptionsCatch<Loguno, JCStatementHolder> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCStatementHolder element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, JCStatementHolder holder, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(holder.element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.VAR, holder.exceptionName))
                .build()
                .create();

        holder.add(methodCall);
    }

    @NoArgsConstructor(staticName = "of")
    @Accessors(fluent = true, chain = true)
    @Setter
    public static class PipedExceptionsHolder {
        private String varName;
        private final Map<JCTree.JCExpression, List<? extends AnnotationTree>> $exceptions = new HashMap<>();
        private CatchTree element;

        public PipedExceptionsHolder putIfAbsentOneException(JCTree.JCExpression exception, List<? extends AnnotationTree> annotations) {
            this.$exceptions.putIfAbsent(exception, annotations);
            return this;
        }
    }

    @NoArgsConstructor(staticName = "of")
    @Accessors(fluent = true, chain = true)
    @Setter
    @Getter
    public static class JCStatementHolder {
        private JCTree.JCAnnotatedType exceptionType;
        private JCTree element;
        private String exceptionName;

        ListBuffer<JCTree.JCStatement> $logMethods = new ListBuffer<>();

        public ListBuffer<JCTree.JCStatement> getLogMethods() {
            return $logMethods;
        }

        void add(JCTree.JCStatement method) {
            $logMethods.append(method);
        }
    }
}
