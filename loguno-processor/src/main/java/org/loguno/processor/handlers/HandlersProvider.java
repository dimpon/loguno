package org.loguno.processor.handlers;

import lombok.SneakyThrows;
import org.reflections.Reflections;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HandlersProvider {

    //private Map<Class<? extends Element>, Map<Class<? extends Annotation>, List<AnnotationHandler<? extends Annotation,? extends Element>>>> handlers;

    //private Map<Class<? extends Element>,  List<AnnotationHandler<? extends Annotation,? extends Element>>> handlers;

    private final Map<Class<? extends Element>, List<AnnotationHandler>> handlers;

    private final Map<Class<? extends Element>, Map<Class<? extends Annotation>, List<AnnotationHandler>>> handlersAnn;

    private final Set<Class<? extends AnnotationHandler<?,?>>> handlerClasses;

    private final Set<Class<? extends Annotation>> supportedAnnotationsClasses;

    public static HandlersProvider create() {
        return new HandlersProvider();
    }

    private HandlersProvider() {

        Reflections reflections = new Reflections("org.loguno.processor.handlers");






        Set<Class<? extends AnnotationHandler>> subTypesOf =
                reflections.getSubTypesOf(AnnotationHandler.class);


        handlerClasses  = Collections.emptySet();

        this.handlers = this.handlerClasses.stream()
                .map(this::create)
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass));


        this.handlersAnn = this.handlerClasses.stream()
                .map(this::create)
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
                        Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));


        this.supportedAnnotationsClasses = this.handlerClasses.stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(this::create).map((Function<AnnotationHandler, Class<? extends Annotation>>) AnnotationHandler::getAnnotationClass).collect(Collectors.toSet());

    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
    private AnnotationHandler<?,?> create(Class<? extends AnnotationHandler<?,?>> clazz) {
        return clazz.newInstance();
    }



    /*@SuppressWarnings("unchecked")
    public <E extends Element> Stream<AnnotationHandler<?,E>> getHandlersByElement(E e) {




        //FIXME
        List<AnnotationHandler<?,E>> annotationHandlers = (List<AnnotationHandler<?,E>>)handlers.get(getElementInterfaceClass(e));

        if (annotationHandlers == null)
            return Stream.empty();

        return null;//annotationHandlers.stream();// annotationHandlers.stream();
    }

    @SuppressWarnings("unchecked")
    public <A extends Annotation, E extends Element> Stream<? extends AnnotationHandler<E>> getHandlersByElementAndAnnotation(A a, E e) {
        return getHandlersByElement(e);


                //.filter(o -> o.getAnnotationClass().equals(a.getClass()));
    }*/



    @SuppressWarnings("unchecked")
    private Class<? extends Element> getElementInterfaceClass(Element e) {
        Class<? extends Element> clazz = e.getClass();

        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> i : interfaces) {
            if (isElementChild(i))
                return (Class<? extends Element>) i;
        }
        throw new RuntimeException();
    }

    public Set<Class<? extends Annotation>> supportedAnnotations() {
        return this.supportedAnnotationsClasses;
    }

    private static boolean isElementChild(Class<?> clazz) {
        Set<Class<?>> asSet = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
        return asSet.contains(Element.class);
    }

    public static void main(String[] args) {
        HandlersProvider handlersProvider = HandlersProvider.create();



        System.out.printf("");

    }


}
