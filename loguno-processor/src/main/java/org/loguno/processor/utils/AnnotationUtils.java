package org.loguno.processor.utils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.tools.javac.tree.JCTree;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.Loguno;
import org.loguno.processor.handlers.VoidAnnotation;
import org.loguno.processor.utils.annotations.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;

@UtilityClass
public class AnnotationUtils {

    private static Map<Class<?>, Class<?>> annotationMapping =
            new HashMap<Class<?>, Class<?>>() {{
                put(Loguno.class, LogunoImpl.class);
                put(Loguno.Logger.class, LogunoLoggerImpl.class);
                put(Loguno.INFO.class, LogunoInfoImpl.class);
                put(Loguno.DEBUG.class, LogunoDebugImpl.class);
                put(Loguno.TRACE.class, LogunoTraceImpl.class);
                put(Loguno.WARN.class, LogunoWarnImpl.class);
                put(Loguno.ERROR.class, LogunoErrorImpl.class);
                put(VoidAnnotation.class, VoidAnnotationImpl.class);
            }};

    /**
     * The method creates the 'real' annotation object based on {@link com.sun.source.tree.AnnotationTree}
     * Now Strings, Strings[], primitives, Enum, Enum[] are supported as annotation member. Classses,  annotations, arrays of primitives are not supported.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A createAnnotationInstance(AnnotationTree annotation, Class<A> annotationType) {

        A annot = (A)annotationMapping.get(annotationType).newInstance();

        Map<String, String[]> customValues = createAnnotationArgsMap(annotation);

        // Extract default values from annotation
        for (Method method : annotationType.getDeclaredMethods()) {

            Class<?> type = method.getReturnType();

            if (customValues.containsKey(method.getName())) {
                Object value = castValue(type, customValues.get(method.getName()));
                if (type.isArray() && !type.getComponentType().isPrimitive()) {
                    Method method1 = annot.getClass().getMethod(method.getName(), type);
                    method1.invoke(annot,type.cast(castToArray(value, type.getComponentType())));
                } else {
                    annot.getClass().getMethod(method.getName(),type).invoke(annot,value);
                }
            }else{
                Object dv = method.getDefaultValue();
                annot.getClass().getMethod(method.getName(),type).invoke(annot,dv);
            }
        }
        return annot;
    }

    @SuppressWarnings("unchecked")
    private <T> T[] castToArray(Object o, Class<T> clazz) {
        Object[] arr = (Object[]) o;
        T[] res = (T[]) Array.newInstance(clazz, arr.length);
        for (int i = 0; i < arr.length; i++) {
            res[i] = (T) arr[i];
        }
        return res;
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

            Object[] constants = clazz.getEnumConstants();

            String va = value[0];
            String[] valu = va.split("\\.");
            final String s = valu[valu.length - 1];

            return Arrays.stream(constants)
                    .filter(s1 -> s1.toString().equals(s))
                    .map(clazz::cast)
                    .findAny().orElseThrow(UnsupportedOperationException::new);
        }

        if (clazz.isArray()) {
            return Arrays.stream(value).map(s -> castValue(clazz.getComponentType(), s)).toArray();
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

            result = aArgument.elems.stream()
                    .map(AnnotationUtils::castElement)
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }

        return result;
    }
}
