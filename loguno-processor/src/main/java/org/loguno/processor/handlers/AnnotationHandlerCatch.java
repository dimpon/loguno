package org.loguno.processor.handlers;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.Tree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Pair;
import org.loguno.Loguno;
import org.loguno.processor.LogunoScanner;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;


public abstract class AnnotationHandlerCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerCatch(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
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

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .param(Pair.of(JCLogMethodBuilder.ParamType.VAR, element.getParameter().getName().toString()))
                .build()
                .create();

        JCTree.JCBlock body = element.getBlock();

        body.stats = body.stats.prepend(methodCall);
    }

    @Handler
    public static class AnnotationHandlerCatchBlock extends AnnotationHandlerBase<VoidAnnotation, LogunoScanner.CatchBlock> {

        public AnnotationHandlerCatchBlock(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, LogunoScanner.CatchBlock block, ClassContext classContext) {

            JCTree.JCCatch catchBlock = block.aCatch();

            Tree.Kind kind = block.aCatch().getParameter().getType().getKind();

            //normal catch
            if (kind == Tree.Kind.IDENTIFIER) {
                Stream<? extends Annotation> annotations = retriever.getTreeAnnotations(catchBlock.getParameter().getModifiers());

                annotations.forEach(ann ->
                        HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), catchBlock)
                                .forEach(h -> h.process(ann, catchBlock, classContext)));
            }



            //catch with piped exceptions
            if (kind == Tree.Kind.UNION_TYPE) {
                com.sun.tools.javac.util.List<JCTree.JCExpression> alternatives = ((JCTree.JCTypeUnion) catchBlock.getParameter().getType())
                        .getTypeAlternatives();

                Name e = catchBlock.getParameter().getName();
                // annotation of the first exception is placed separately, like annotations of whole exceptions group
                java.util.List<? extends AnnotationTree> annotations_1 = catchBlock.getParameter().getModifiers().getAnnotations();
                JCTree.JCExpression exceptionClass_1 = alternatives.get(0);

                final AnnotationHandlerPipedExceptionsCatch.PipedExceptionsHolder pipedExceptionsHolder = AnnotationHandlerPipedExceptionsCatch.PipedExceptionsHolder.of()
                        .varName(e.toString())
                        .element(catchBlock)
                        .putIfAbsentOneException(exceptionClass_1, annotations_1);

                alternatives.forEach(exception -> {
                    if (exception instanceof JCTree.JCAnnotatedType) {
                        JCTree.JCAnnotatedType annotated = (JCTree.JCAnnotatedType) exception;
                        pipedExceptionsHolder.putIfAbsentOneException(annotated.underlyingType, annotated.getAnnotations());
                    }
                });

                HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(VoidAnnotation.class, pipedExceptionsHolder)
                        .forEach(h -> h.process(JCTreeUtils.VOID_ANN, pipedExceptionsHolder, classContext));
            }

        }
    }
}
