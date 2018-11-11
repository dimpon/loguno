package org.loguno.processor.utils;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationKey;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.VoidAnnotation;
import org.loguno.processor.utils.annotations.VoidAnnotationImpl;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.lang.reflect.Field;
import java.util.*;
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

    public static final VoidAnnotation VOID_ANN = new VoidAnnotationImpl();


    public String getRepeatPart(String messagePattern) {
        Pattern p = Pattern.compile(REPEAT_PATTERN);
        Matcher m = p.matcher(messagePattern);
        if (m.find()) {
            String found = m.group();
            return found.substring(1, found.length() - 1);
        }
        return "";
    }


    public boolean hasBody(final JCTree element) {
        Class bodyClass = JCTree.JCBlock.class;
        Class staClass = JCTree.JCStatement.class;
        Field[] declaredFields = element.getClass().getDeclaredFields();

        return Arrays.stream(declaredFields)
                .filter(field -> field.getName().equals("body"))
                .filter(field -> field.getType().equals(bodyClass) || field.getType().equals(staClass))
                .findAny().isPresent();
    }

    @SneakyThrows
    public JCTree.JCStatement getBody(final JCTree element) {
        Class bodyClass = JCTree.JCBlock.class;
        Class staClass = JCTree.JCStatement.class;
        Field[] declaredFields = element.getClass().getDeclaredFields();

        Field body = Arrays.stream(declaredFields)
                .filter(field -> field.getName().equals("body"))
                .filter(field -> field.getType().equals(bodyClass) || field.getType().equals(staClass))
                .findFirst().orElseThrow(() -> {
                    throw new RuntimeException("No body field in " + element);
                });
        return (JCTree.JCStatement) body.get(element);
    }

    private boolean isConstructorWithSuper(ExecutableElement method, JCTree.JCBlock body) {

        if (method != null)
            return (method.getKind() == ElementKind.CONSTRUCTOR &&
                    body.stats.size() > 0 &&
                    body.stats.get(0) != null &&
                    body.stats.get(0).toString().startsWith("super("));
        else
            return (body.stats.size() > 0 &&
                    body.stats.get(0) != null &&
                    body.stats.get(0).toString().startsWith("super("));
    }


    public com.sun.tools.javac.util.List<JCTree.JCStatement> generateNewBody(JCTree parentheses, JCTree.JCStatement block, JCTree.JCStatement methodCall) {
        JCTree.JCBlock body = (JCTree.JCBlock) block;

        if (parentheses instanceof JCTree.JCMethodDecl) {
            JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) parentheses;
            if (JCTreeUtils.isConstructorWithSuper(methodDecl.sym, body)) {

                ListBuffer<JCTree.JCStatement> bodyNew = new ListBuffer<>();
                bodyNew.append(body.stats.get(0));
                bodyNew.append(methodCall);

                for (int i = 1; i < body.stats.size(); i++) {
                    bodyNew.append(body.stats.get(i));
                }

                return bodyNew.toList();
            }
        }
        return body.stats.prepend(methodCall);
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


}
