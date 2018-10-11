package org.loguno.processor.utils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.tools.javac.tree.JCTree;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import sun.reflect.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Ponomarev
 */
@UtilityClass
public class JCTreeUtils {


    /**
     *
     * The method creates the 'real' annotation object based on {@link com.sun.source.tree.AnnotationTree}
     * Now Strings, Strings[] and primitivesa are supported as annotation memmber.
     *
     */
    @SuppressWarnings("unchecked")
    public  <A extends Annotation> A createAnnotationInstance(AnnotationTree annotation, Class<A> annotationType) {

        Map<String, String[]> customValues = createAnnotationArgsMap(annotation);

        Map<String, Object> values = new LinkedHashMap<>();

        //Extract default values from annotation
        for (Method method : annotationType.getDeclaredMethods()) {
            values.put(method.getName(), method.getDefaultValue());
            Class<?> type = method.getReturnType();
            if (customValues.containsKey(method.getName())) {
                Object o = castValue(type, customValues.get(method.getName()));
                values.put(method.getName(), o);
            }
        }

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
            throw new UnsupportedOperationException();
        }

        if (clazz.isArray()) {
            return value;
        }

        return value[0];
    }

    private Map<String, String[]> createAnnotationArgsMap(AnnotationTree annotation) {
        List<? extends ExpressionTree> arguments = annotation.getArguments();

        Map<String, String[]> values = new LinkedHashMap<>();

        for (ExpressionTree argument : arguments) {
            if (argument instanceof JCTree.JCAssign) {
                JCTree.JCAssign aArgument = (JCTree.JCAssign) argument;
                String name = ((JCTree.JCIdent) aArgument.lhs).getName().toString();
                values.put(name, castElement(aArgument.rhs));
            } else {
                values.put("value", castElement(argument));
            }
        }
        return values;
    }


    private String[] castElement(ExpressionTree argument) {

        String[] result = new String[0];

        if (argument instanceof JCTree.JCLiteral) {
            JCTree.JCLiteral aArgument = (JCTree.JCLiteral) argument;
            result = new String[]{aArgument.getValue().toString()};

        } else if (argument instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess aArgument = (JCTree.JCFieldAccess) argument;
            result = new String[]{aArgument.toString()};

        } else if (argument instanceof JCTree.JCNewArray) {
            JCTree.JCNewArray aArgument = (JCTree.JCNewArray) argument;

            List<String[]> collect = aArgument.elems.stream()
                    .map(JCTreeUtils::castElement).collect(Collectors.toList());

            result = aArgument.elems.stream()
                    .map(JCTreeUtils::castElement)
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }

        return result;
    }


}
