package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.ListBuffer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.loguno.Loguno;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.AnnotationHandlerMultipleExceptionsCatch;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.JCTreeUtils;
import sun.reflect.annotation.AnnotationParser;

import javax.lang.model.element.Name;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dmitrii Ponomarev
 */
@AllArgsConstructor
public class LogunoMethodBodyVisitor extends TreeScanner<Void, ClassContext> {


    //private final JavacProcessingEnvironment environment;
    private final HandlersProvider handlersProvider;

    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {

        List<? extends AnnotationTree> variableAnnotations = variableTree.getModifiers().getAnnotations();
        variableAnnotations.forEach(annotation -> {
            findHandlerAndCall(annotation, variableTree, classContext);
        });
        return super.visitVariable(variableTree, classContext);
    }

    private <E> void findHandlerAndCall(AnnotationTree annotation, E element, ClassContext classContext) {

        Tree annotationType = annotation.getAnnotationType();
        String className = annotationType.toString().replace(".", "$");
        Optional<Class<? extends Annotation>> annClass = handlersProvider.getAnnotationClassByName(className);

        if (annClass.isPresent()) {
            Stream<? extends AnnotationHandler<?, E>> handlers = handlersProvider.getHandlersByElementAndAnnotation(annClass.get(), element);
            Annotation annotationObj = JCTreeUtils.createAnnotationInstance(annotation, annClass.get());
            handlers.forEach(handler -> {
                handler.process(annotationObj, element, classContext);
            });
        }
    }

    public Void visitCatch(CatchTree catchBlock, ClassContext classContext) {

        Tree.Kind kind = catchBlock.getParameter().getType().getKind();

        if (kind == Tree.Kind.IDENTIFIER) {

            List<? extends AnnotationTree> variableAnnotations = catchBlock.getParameter().getModifiers().getAnnotations();

            variableAnnotations.forEach(annotation -> {
                findHandlerAndCall(annotation, catchBlock, classContext);
            });
        }



        if (kind == Tree.Kind.UNION_TYPE) {
            com.sun.tools.javac.util.List<JCTree.JCExpression> typeAlternatives = ((JCTree.JCTypeUnion) catchBlock.getParameter().getType()).getTypeAlternatives();

            Name e = catchBlock.getParameter().getName();
            BlockTree block = catchBlock.getBlock();

            List<? extends AnnotationTree> annotations1 = catchBlock.getParameter().getModifiers().getAnnotations();
            JCTree.JCExpression exceptionClass1 = typeAlternatives.get(0);

            annotations1.forEach(annotation -> {
                findHandlerAndCall(annotation, AnnotationHandlerMultipleExceptionsCatch
                        .ExceptionTO.of(e, (JCTree.JCBlock) block, exceptionClass1, (JCTree) catchBlock), classContext);
            });


            for (int i = 1; i < typeAlternatives.size(); i++) {

                JCTree.JCExpression exception = typeAlternatives.get(i);
                if (exception instanceof JCTree.JCAnnotatedType) {
                    JCTree.JCAnnotatedType annException = (JCTree.JCAnnotatedType) exception;
                    JCTree.JCExpression exceptionClass = annException.underlyingType;
                    com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations = annException.getAnnotations();

                    for (JCTree.JCAnnotation annotation : annotations) {
                        findHandlerAndCall(annotation, AnnotationHandlerMultipleExceptionsCatch
                                .ExceptionTO.of(e, (JCTree.JCBlock) block, exceptionClass, (JCTree) catchBlock), classContext);
                    }

                }
            }
        }

        return super.visitCatch(catchBlock, classContext);
    }


    @Override
    public Void visitBlock(BlockTree block, ClassContext classContext) {


       /* Class<org.loguno.Loguno> aClass = (Class<org.loguno.Loguno>) this.getClass().getClassLoader().loadClass("org.loguno.Loguno");

        Map<String, Object> values = new HashMap<>();
        values.put("value", "some");

        Loguno annotationInstance = createAnnotationInstance(values, aClass);

        Stream<? extends AnnotationHandler<?, BlockTree>> handlersByElementAndAnnotation = handlersProvider.getHandlersByElementAndAnnotation(aClass, block);

        handlersByElementAndAnnotation.forEach(o -> o.process(annotationInstance, block, classContext));*/

        try {
            classContext.getBlocks().add(block);
            return super.visitBlock(block, classContext);
        } finally {
            classContext.getBlocks().removeLast();
        }
    }
}
