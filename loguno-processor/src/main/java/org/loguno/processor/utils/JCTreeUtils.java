package org.loguno.processor.utils;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationKey;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.handlers.VoidAnnotation;
import sun.reflect.annotation.AnnotationParser;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.loguno.processor.configuration.ConfigurationKeys.CLASS_PATTERN;
import static org.loguno.processor.configuration.ConfigurationKeys.METHOD_PATTERN;

/**
 * @author Dmitrii Ponomarev
 */
@UtilityClass
public class JCTreeUtils {

    public static final String REPEAT_PATTERN = "\\[(.*?)\\]";

    private static VoidAnnotation VOID_ANN = (VoidAnnotation) AnnotationParser.annotationForMap(VoidAnnotation.class, Collections.emptyMap());

    public <E> void findHandlersAndCall(AnnotationTree annotation, E element, ClassContext classContext) {

        final HandlersProvider handlersProvider = HandlersProvider.instance();
        Tree annotationType = annotation.getAnnotationType();
        String className = annotationType.toString().replace(".", "$");
        Optional<Class<? extends Annotation>> annClass = handlersProvider.getAnnotationClassByName(className);

        if (annClass.isPresent()) {
            Stream<? extends AnnotationHandler<?, E>> handlers = handlersProvider.getHandlersByElementAndAnnotation(annClass.get(), element);
            Annotation annotationObj = AnnotationUtils.createAnnotationInstance(annotation, annClass.get());
            handlers.forEach(handler -> {
                handler.process(annotationObj, element, classContext);
            });
        }
    }

    public <E> void findVoidHandlersAndCall(E element, ClassContext classContext) {
        final HandlersProvider handlersProvider = HandlersProvider.instance();
        Stream<? extends AnnotationHandler<?, E>> handlers = handlersProvider.getHandlersByElementAndAnnotation(VoidAnnotation.class, element);
        handlers.forEach(handler -> {
            handler.process(JCTreeUtils.VOID_ANN, element, classContext);
        });
    }

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





}
