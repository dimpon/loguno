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
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.JCTreeUtils;
import sun.reflect.annotation.AnnotationParser;

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
public class LogunoLocalVariableVisitor extends TreeScanner<Void, ClassContext> {


    //private final JavacProcessingEnvironment environment;
    private final HandlersProvider handlersProvider;

    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {

        List<? extends AnnotationTree> variableAnnotations = variableTree.getModifiers().getAnnotations();

        variableAnnotations.forEach(annotation -> {

            Tree annotationType = annotation.getAnnotationType();
            String className = annotationType.toString().replace(".", "$");
            Optional<Class<? extends Annotation>> annClass = handlersProvider.getAnnotationClassByName(className);


            if (annClass.isPresent()) {
                Stream<? extends AnnotationHandler<?, VariableTree>> handlers = handlersProvider.getHandlersByElementAndAnnotation(annClass.get(), variableTree);
                Annotation annotationObj = JCTreeUtils.createAnnotationInstance(annotation, annClass.get());
                handlers.forEach(handler -> {
                    handler.process(annotationObj, variableTree, classContext);
                });
            }
        });
        return super.visitVariable(variableTree, classContext);
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
            classContext.setCurrentBlock(block);
            return super.visitBlock(block, classContext);
        } finally {
            classContext.setCurrentBlock(null);
        }
    }
}
