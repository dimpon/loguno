package org.loguno.processor.utils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationKey;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.ClassContext;
import sun.reflect.annotation.AnnotationParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.loguno.processor.configuration.ConfigurationKeys.CLASS_PATTERN;
import static org.loguno.processor.configuration.ConfigurationKeys.METHOD_PATTERN;

/**
 * @author Dmitrii Ponomarev
 */
@UtilityClass
public class JCTreeUtils {

    public static final String REPEAT_PATTERN = "\\[(.*?)\\]";

    public String getRepeatPart(String messagePattern) {
        Pattern p = Pattern.compile(REPEAT_PATTERN);
        Matcher m = p.matcher(messagePattern);
        if (m.find()) {
            String found = m.group();
            return found.substring(1, found.length() - 1);
        }
        return "";
    }


    private boolean isMethodConstructorWithSuper(ExecutableElement method, JCTree.JCBlock body) {
        return (method.getKind() == ElementKind.CONSTRUCTOR &&
                body.stats.size() > 0 &&
                body.stats.get(0) != null &&
                body.stats.get(0).toString().contains("super"));
    }

    public com.sun.tools.javac.util.List<JCTree.JCStatement> generateNewMethodBody(ExecutableElement method, Trees trees, JCTree.JCStatement methodCall) {

        MethodTree methodTree = trees.getTree(method);
        JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();

        if (JCTreeUtils.isMethodConstructorWithSuper(method, body)) {

            ListBuffer<JCTree.JCStatement> bodyNew = new ListBuffer<>();
            bodyNew.append(body.stats.get(0));
            bodyNew.append(methodCall);

            for (int i = 1; i < body.stats.size(); i++) {
                bodyNew.append(body.stats.get(i));
            }

            return bodyNew.toList();
        } else {
            return body.stats.prepend(methodCall);
        }
    }

    public String message(String[] valueFromAnn, ConfigurationKey<String> key, ClassContext context) {
        return JCTreeUtils.tryToInsertClassAndMethodName(getMessageTemplate(valueFromAnn, key), context);
    }

    /**
     * returns value from array, if exists. Otherwise from config.
     */
    private String getMessageTemplate(String[] value, ConfigurationKey<String> key) {
        Configuration conf = ConfiguratorManager.getInstance().getConfiguration();
        return (value.length > 0 && !value[0].isEmpty()) ? value[0] : conf.getProperty(key);
    }

    /**
     * Tries to insert into message class name and method name.
     */
    private String tryToInsertClassAndMethodName(String message, ClassContext context) {
        return message.replace(CLASS_PATTERN, context.getClasses().getLast())
                .replace(METHOD_PATTERN, context.getMethods().getLast());
    }

    /**
     * The method creates the 'real' annotation object based on {@link com.sun.source.tree.AnnotationTree}
     * Now Strings, Strings[] and primitives are supported as annotation member. Classses, enums, annotations are not supported.
     */
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A createAnnotationInstance(AnnotationTree annotation, Class<A> annotationType) {

        Map<String, String[]> customValues = createAnnotationArgsMap(annotation);

        Map<String, Object> values = new LinkedHashMap<>();

        // Extract default values from annotation
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

            result = aArgument.elems.stream()
                    .map(JCTreeUtils::castElement)
                    .flatMap(Arrays::stream)
                    .toArray(String[]::new);
        }

        return result;
    }

}
