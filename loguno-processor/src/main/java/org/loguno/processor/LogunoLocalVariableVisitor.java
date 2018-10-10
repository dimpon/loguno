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
import sun.reflect.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dmitrii Ponomarev
 */
@AllArgsConstructor
public class LogunoLocalVariableVisitor extends TreeScanner<Void, ClassContext> {


    private final JavacProcessingEnvironment environment;
    private final HandlersProvider handlersProvider;

   /* @Override
    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {

        Context context = environment.getContext();
        TreeMaker instance = TreeMaker.instance(context);

        JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl) variableTree;

        if(variable.sym == null){//it catches method parameters also. local vars has sym = null.

            List<JCTree.JCAnnotation> annotations = variable.getModifiers().getAnnotations();

            //instance.

            annotations.forEach(a -> {

                if(a.toString().equals("@Loguno()")){

                    ListBuffer newStatements = new ListBuffer();

                }

            });
            //annotations.stream().filter(a -> a.attribute.)
        }

        return super.visitVariable(variableTree, classContext);
    }*/

    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {

        List<? extends AnnotationTree> annotations = variableTree.getModifiers().getAnnotations();

        annotations.forEach(o -> {
            Tree annotationType = o.getAnnotationType();
            Tree.Kind kind = annotationType.getKind();

            String className = annotationType.toString().replace(".", "$");


            Optional<Class<? extends Annotation>> annClass = handlersProvider.getAnnotationClassByName(className);


            if (annClass.isPresent()) {
                //Class<? extends Annotation> annClass = getAnnClass(className);
                Stream<? extends AnnotationHandler<?, VariableTree>> handlersByElementAndAnnotation = handlersProvider.getHandlersByElementAndAnnotation(annClass.get(), variableTree);
                List<? extends AnnotationHandler<?, VariableTree>> collect = handlersByElementAndAnnotation.collect(Collectors.toList());
                int size = collect.size();
            }

        });


        return super.visitVariable(variableTree, classContext);
    }


    @SneakyThrows
    @Override
    public Void visitBlock(BlockTree var1, ClassContext var2) {


        Class<org.loguno.Loguno> aClass = (Class<org.loguno.Loguno>) this.getClass().getClassLoader().loadClass("org.loguno.Loguno");

        Map<String, Object> values = new HashMap<>();
        values.put("value", "some");

        Loguno annotationInstance = createAnnotationInstance(values, aClass);

        Stream<? extends AnnotationHandler<?, BlockTree>> handlersByElementAndAnnotation = handlersProvider.getHandlersByElementAndAnnotation(aClass, var1);

        handlersByElementAndAnnotation.forEach(o -> o.process(annotationInstance, var1, var2));


        return super.visitBlock(var1, var2);
    }

    @Override
    public Void visitMethod(MethodTree var1, ClassContext var2) {
        ModifiersTree modifiers = var1.getModifiers();

        Tree returnType = var1.getReturnType();
        Iterable typeParameters = var1.getTypeParameters();
        Iterable parameters = var1.getParameters();
        Tree receiverParameter = var1.getReceiverParameter();
        Iterable aThrows = var1.getThrows();
        Tree body = var1.getBody();
        Tree defaultValue = var1.getDefaultValue();
        return super.visitMethod(var1, var2);
    }


  /*  @Override
    public Void visitAnnotation(AnnotationTree var1, ClassContext var2) {
        return super.visitAnnotation(var1, var2);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree var1, ClassContext var2) {
        return super.visitAnnotatedType(var1, var2);
    }*/


    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A createAnnotationInstance(Map<String, Object> customValues, Class<A> annotationType) {

        Map<String, Object> values = new HashMap<>();

        //Extract default values from annotation
        for (Method method : annotationType.getDeclaredMethods()) {
            values.put(method.getName(), method.getDefaultValue());
        }

        //Populate required values
        values.putAll(customValues);

        return (A) AnnotationParser.annotationForMap(annotationType, values);
    }

    //@SneakyThrows
    @SuppressWarnings("unchecked")
    private Class<? extends Annotation> getAnnClass(String className) {
        try {
            return (Class<? extends Annotation>) this.getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


}
