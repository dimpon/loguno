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


            String className = annotationType.toString().replace(".", "$");


            Optional<Class<? extends Annotation>> annClass = handlersProvider.getAnnotationClassByName(className);


            if (annClass.isPresent()) {
                //Class<? extends Annotation> annClass = getAnnClass(className);
                Stream<? extends AnnotationHandler<?, VariableTree>> handlers = handlersProvider.getHandlersByElementAndAnnotation(annClass.get(), variableTree);


                //Map<String, Object> args = new LinkedHashMap<>();

                System.out.println("o=" + o);

                Map<String, String[]> args = createArgs(o);

                /*o.getArguments().forEach(argument -> {

                    System.out.println("argument=" + argument);

                    JCTree.JCAssign aargument = (JCTree.JCAssign) argument;
                    args.put(((JCTree.JCIdent) aargument.lhs).getName().toString(), ((JCTree.JCLiteral) aargument.rhs).getValue());
                });*/
                Annotation annotation = createAnnotationInstance(args, annClass.get());
                handlers.forEach(handler -> {
                    handler.process(annotation, variableTree, classContext);
                });
            }
        });
        return super.visitVariable(variableTree, classContext);
    }


    private Map<String, String[]> createArgs(AnnotationTree annotation) {
        List<? extends ExpressionTree> arguments = annotation.getArguments();

        Map<String, String[]> values = new LinkedHashMap<>();

        for (ExpressionTree argument : arguments) {

            if (argument instanceof JCTree.JCLiteral) {
                JCTree.JCLiteral aArgument = (JCTree.JCLiteral) argument;
                values.put("value", new String[]{aArgument.getValue().toString()});

            } else if (argument instanceof JCTree.JCFieldAccess) {
                JCTree.JCFieldAccess aArgument = (JCTree.JCFieldAccess) argument;
                values.put("value", new String[]{aArgument.toString()});

            } else if (argument instanceof JCTree.JCNewArray) {
                JCTree.JCNewArray aArgument = (JCTree.JCNewArray) argument;
                String[] objects = aArgument.elems.stream().map(e -> {

                    if (e instanceof JCTree.JCFieldAccess)
                        return ((JCTree.JCFieldAccess) e).toString();

                    if (e instanceof JCTree.JCLiteral)
                        return ((JCTree.JCLiteral) e).getValue().toString();

                    return "";

                }).toArray(String[]::new);
                values.put("value", objects);

            } else if (argument instanceof JCTree.JCAssign) {
                JCTree.JCAssign aArgument = (JCTree.JCAssign) argument;
                String name = ((JCTree.JCIdent) aArgument.lhs).getName().toString();

                if (aArgument.rhs instanceof JCTree.JCLiteral) {
                    JCTree.JCLiteral val = (JCTree.JCLiteral) aArgument.rhs;
                    values.put(name, new String[]{val.getValue().toString()});

                } else if (aArgument.rhs instanceof JCTree.JCFieldAccess) {
                    JCTree.JCFieldAccess val = (JCTree.JCFieldAccess) aArgument.rhs;
                    values.put(name, new String[]{val.toString()});

                } else if (aArgument.rhs instanceof JCTree.JCNewArray) {
                    JCTree.JCNewArray val = (JCTree.JCNewArray) aArgument.rhs;
                    String[] objects = val.elems.stream().map(e -> {

                        if (e instanceof JCTree.JCFieldAccess)
                            return ((JCTree.JCFieldAccess) e).toString();

                        if (e instanceof JCTree.JCLiteral)
                            return ((JCTree.JCLiteral) e).getValue().toString();

                        return "";

                    }).toArray(String[]::new);
                    values.put(name, objects);
                }
            }
        }

        return values;
    }


    //@SneakyThrows
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


    @SuppressWarnings("unchecked")
    private <A extends Annotation> A createAnnotationInstance(Map<String, String[]> customValues, Class<A> annotationType) {

        Map<String, Object> values = new LinkedHashMap<>();

        //Extract default values from annotation
        for (Method method : annotationType.getDeclaredMethods()) {
            values.put(method.getName(), method.getDefaultValue());
            Class<?> type = method.getReturnType();


            System.out.println("type=" + type);

            if (customValues.containsKey(method.getName())) {
                //values.put(method.getName(), returnType.cast(customValues.get(method.getName())));

                Object o = castValue(type, customValues.get(method.getName()));
                values.put(method.getName(), o);
            }
        }

        //Populate required values
        //values.putAll(customValues);

        return (A) AnnotationParser.annotationForMap(annotationType, values);
    }

    @SneakyThrows
    private Object castValue(Class<?> clazz, String... value) {

        if (clazz.isPrimitive()) {

            switch (clazz.getName()) {

                case "boolean":
                    return Boolean.valueOf(value[0]);

                case "int":
                    return Integer.valueOf(value[0]);

                case "long":
                    return Long.valueOf(value[0]);

                case "double":
                    return Double.valueOf(value[0]);

                case "float":
                    return Float.valueOf(value[0]);

                case "byte":
                    return Byte.valueOf(value[0]);

            }
        }

        if (clazz.isEnum()) {
            String enumPath = value[0];

            String fieldName = enumPath.substring(enumPath.lastIndexOf(".") + 1);

            String className = enumPath.substring(0, enumPath.lastIndexOf(".")).replace(".", "$");



            Class<?> aClass = this.getClass().getClassLoader().loadClass(className);

            return Arrays.stream(aClass.getEnumConstants()).filter(o -> o.toString().equals(fieldName)).findAny().get();
        }

        if (clazz.isArray()) {
            int length = value.length;

        }

        if (clazz.isLocalClass()) {
            return this.getClass().getClassLoader().loadClass(value[0]);
        }
        return value[0];
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
