package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import lombok.SneakyThrows;
import org.reflections.Reflections;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class scans package with annotation handlers, instanciates them, saves in {@link #handlers}
 * Returns the list of supported annotations, returns handlers by distinct Element and annotation.
 */
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
                .map(c -> createHandler(c, environment))
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
                        Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));

        this.supportedAnnotations = this.handlers.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public <E extends Element> Stream<? extends AnnotationHandler<?, E>> getHandlersByElementAndAnnotation(Class<? extends Annotation> a, E e) {
        return handlers
                .getOrDefault(keyClass(e), Collections.emptyMap())
                .getOrDefault(a, Collections.emptyList())
                .stream()
                .map(h -> (AnnotationHandler<?, E>) h);
    }

    public Set<Class<? extends Annotation>> supportedAnnotations() {
        return this.supportedAnnotations;
    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchMethodException.class, InvocationTargetException.class})
    private AnnotationHandler<?, ?> createHandler(Class<? extends AnnotationHandler<?, ?>> clazz, JavacProcessingEnvironment environment) {
        return clazz.getConstructor(JavacProcessingEnvironment.class).newInstance(environment);
    }

    //todo if performance is slow try https://github.com/atteo/classindex
    @SuppressWarnings("unchecked")
    private Stream<Class<? extends AnnotationHandler<? extends Annotation, ? extends Element>>> getAnnotationHandlersClasses() {
        Reflections reflections = new Reflections(HANDLERS_PACKAGE);
        Set<Class<? extends AnnotationHandler>> handlers = reflections.getSubTypesOf(AnnotationHandler.class);
        return handlers.stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> (Class<? extends AnnotationHandler<? extends Annotation, ? extends Element>>) c);
    }

    /**
     * Finds the interface class which shows the real type of element.
     * The class is used as key for getting handlers from {@link HandlersProvider#handlers}
     *
     * @param e Element of class
     * @return class if interface which is subtype of {@link Element}
     */
    @SuppressWarnings("unchecked")
    private Class<? extends Element> keyClass(Element e) {
        Class<? extends Element> clazz = e.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> i : interfaces) {
            if (isClassSubtypeOfElement(i))
                return (Class<? extends Element>) i;
        }
        throw new RuntimeException("element doesn't implement the *Element interface");
    }

    private static boolean isClassSubtypeOfElement(Class<?> clazz) {
        Set<Class<?>> asSet = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        return asSet.contains(Element.class);
    }
}
