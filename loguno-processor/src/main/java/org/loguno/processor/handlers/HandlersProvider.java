package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HandlersProvider {

    private static final String HANDLERS_PACKAGE = "org.loguno.processor.handlers";

    private final Map<Class<? extends Element>, Map<Class<? extends Annotation>, List<AnnotationHandler<? extends Annotation, ? extends Element>>>> handlers;

    private final Set<Class<? extends Annotation>> supportedAnnotations;

    public static HandlersProvider create(JavacProcessingEnvironment environment) {
        return new HandlersProvider(environment);
    }

    private HandlersProvider(JavacProcessingEnvironment environment) {

        List<Class<? extends AnnotationHandler<? extends Annotation, ? extends Element>>> allHandlersClasses =
                getAnnotationHandlersClasses().collect(Collectors.toList());




        this.handlers = allHandlersClasses.stream()
                .map(c -> create(c, environment))
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
                        Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));


        this.supportedAnnotations = allHandlersClasses.stream()
                .map(c -> create(c, environment))
                .map((Function<AnnotationHandler, Class<? extends Annotation>>) AnnotationHandler::getAnnotationClass)
                .collect(Collectors.toSet());

    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchMethodException.class, InvocationTargetException.class})
    private AnnotationHandler<?, ?> create(Class<? extends AnnotationHandler<?, ?>> clazz, JavacProcessingEnvironment environment) {
        return clazz.getConstructor(JavacProcessingEnvironment.class).newInstance(environment);
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<? extends AnnotationHandler<? extends Annotation, ? extends Element>>> getAnnotationHandlersClasses() {
        Reflections reflections = new Reflections(HANDLERS_PACKAGE);
        Set<Class<? extends AnnotationHandler>> handlers = reflections.getSubTypesOf(AnnotationHandler.class);
        return handlers.stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> (Class<? extends AnnotationHandler<? extends Annotation, ? extends Element>>) c);
    }


    @SuppressWarnings("unchecked")
    public <E extends Element> Stream<? extends AnnotationHandler<?, E>> getHandlersByElementAndAnnotation(Class<? extends Annotation> a, E e) {
        return handlers
                .getOrDefault(keyClass(e), Collections.emptyMap())
                .getOrDefault(a, Collections.emptyList()).stream().map(h -> (AnnotationHandler<?, E>) h);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends Element> keyClass(Element e) {
        Class<? extends Element> clazz = e.getClass();

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> i : interfaces) {
            if (isElementChild(i))
                return (Class<? extends Element>) i;
        }
        throw new RuntimeException();
    }

    public Set<Class<? extends Annotation>> supportedAnnotations() {
        return this.supportedAnnotations;
    }

    private static boolean isElementChild(Class<?> clazz) {
        Set<Class<?>> asSet = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        return asSet.contains(Element.class);
    }
}
