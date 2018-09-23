package org.loguno.processor.handlers;

import lombok.SneakyThrows;
import org.reflections.Reflections;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public final class HandlersProvider {

    private Map<Class<? extends Element>, Map<Class<? extends Annotation>, List<AnnotationHandler>>> handlers;
    private Set<Class<? extends AnnotationHandler>> handlerClasses;

    public static HandlersProvider create() {
        return new HandlersProvider();
    }

    private HandlersProvider() {
        Reflections reflections = new Reflections("org.loguno.processor.handlers");
        this.handlerClasses = reflections.getSubTypesOf(AnnotationHandler.class);

        this.handlers = this.handlerClasses.stream().filter(c -> !Modifier.isAbstract(c.getModifiers())).map(this::create)
                .collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
                        Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));

    }

    @SneakyThrows({InstantiationException.class, IllegalAccessException.class})
    private AnnotationHandler create(Class<? extends AnnotationHandler> clazz) {
        return clazz.newInstance();
    }


    public Optional<Map<Class<? extends Annotation>, List<AnnotationHandler>>> getByElement(Element e) {
        return handlers.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(e)).map(Map.Entry::getValue).findFirst();
    }


}
