package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import lombok.SneakyThrows;
import org.loguno.processor.utils.ScanPackageUtils;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class scans package with annotation handlers, instanciates them, saves in {@link #handlers}
 * Returns the list of supported annotations, returns handlers by distinct Type and annotation.
 */
public final class HandlersProvider {

    private static final String HANDLERS_PACKAGE = "org.loguno.processor.handlers";

    private final Map<Class<?>, Map<Class<? extends Annotation>, List<AnnotationHandler>>> handlers;

    private final Set<Class<? extends Annotation>> supportedAnnotations;

    private final Map<String, Class<? extends Annotation>> supportedAnnotationsWithStringNames;

    private static HandlersProvider INSTANCE;

    public static HandlersProvider create(JavacProcessingEnvironment environment) {
        INSTANCE = new HandlersProvider(environment);
        return INSTANCE;
    }

    public static HandlersProvider instance() {
        return INSTANCE;
    }

    private HandlersProvider(JavacProcessingEnvironment environment) {

        List<Class<? extends AnnotationHandler>> allHandlersClasses = getAnnotationHandlersClasses()
                .collect(Collectors.toList());

        this.handlers = allHandlersClasses.stream()
                .map(c -> createHandler(c, environment))
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
                        Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));

        this.supportedAnnotations = this.handlers.entrySet().stream()
                .map(Map.Entry::getValue)
                .flatMap(m -> m.keySet().stream())
                .collect(Collectors.toSet());

        supportedAnnotationsWithStringNames = new HashMap<>();

        String packageToCut = "org.loguno.";
        Map<String, Class<? extends Annotation>> ann1 = this.supportedAnnotations.stream().collect(Collectors.toMap(o -> o.getName().replace("$", "."), Function.identity()));
        Map<String, Class<? extends Annotation>> ann2 = this.supportedAnnotations.stream().collect(Collectors.toMap(o -> o.getName().replace("$", ".").replace(packageToCut, ""), Function.identity()));

        supportedAnnotationsWithStringNames.putAll(ann1);
        supportedAnnotationsWithStringNames.putAll(ann2);

    }

    @SuppressWarnings("unchecked")
    public <E> Stream<? extends AnnotationHandler<?, E>> getHandlersBeforeByElementAndAnnotation(Class<? extends Annotation> a, E e) {
        return handlers
                .getOrDefault(keyClass(e), Collections.emptyMap())
                .getOrDefault(a, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(h -> h.getClass().getAnnotation(Handler.class).order()))
                .filter(h -> h.getClass().getAnnotation(Handler.class).value()== Handler.RunOrder.BEFORE)
                .map(h -> (AnnotationHandler<?, E>) h);
    }

    @SuppressWarnings("unchecked")
    public <E> Stream<? extends AnnotationHandler<?, E>> getHandlersAfterByElementAndAnnotation(Class<? extends Annotation> a, E e) {
        return handlers
                .getOrDefault(keyClass(e), Collections.emptyMap())
                .getOrDefault(a, Collections.emptyList())
                .stream()
                .sorted(Comparator.comparing(h -> h.getClass().getAnnotation(Handler.class).order()))
                .filter(h -> h.getClass().getAnnotation(Handler.class).value()== Handler.RunOrder.AFTER)
                .map(h -> (AnnotationHandler<?, E>) h);
    }

    public Set<Class<? extends Annotation>> supportedAnnotations() {
        return this.supportedAnnotations;
    }

    public Optional<Class<? extends Annotation>> getAnnotationClassByName(final String name) {
        return Optional.ofNullable(supportedAnnotationsWithStringNames.get(name));
    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class, NoSuchMethodException.class, InvocationTargetException.class})
    private AnnotationHandler createHandler(Class<? extends AnnotationHandler> clazz, JavacProcessingEnvironment environment) {
        return clazz.getConstructor(JavacProcessingEnvironment.class).newInstance(environment);
    }

    // todo if performance is slow try https://github.com/atteo/classindex https://github.com/classgraph/classgraph
    @SuppressWarnings("unchecked")
    private Stream<Class<? extends AnnotationHandler>> getAnnotationHandlersClasses() {
        List<Class<? extends AnnotationHandler>> collect = ScanPackageUtils.getHandlersClasses()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .filter(c -> c.isAnnotationPresent(Handler.class))
                .map(c -> (Class<? extends AnnotationHandler>) c)
                .collect(Collectors.toList());

        return collect.stream();
    }

    /**
     * Finds the interface class which shows the real type of element.
     * The class is used as key for getting handlers from {@link HandlersProvider#handlers}
     *
     * @param e Element of class
     * @return class if interface which is subtype of {@link Element}
     */
    @SuppressWarnings("unchecked")
    private Class<?> keyClass(Object e) {
        Class<?> clazz = e.getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> i : interfaces) {
            if (isClassSubtypeOfElement(i))
                return i;
        }
        return e.getClass();
    }

    private static boolean isClassSubtypeOfElement(Class<?> clazz) {
        Set<Class<?>> asSet = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        return asSet.contains(Element.class);
    }
}
