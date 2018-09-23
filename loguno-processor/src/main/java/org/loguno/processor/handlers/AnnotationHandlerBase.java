package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AnnotationHandlerBase<A extends Annotation, E extends Element> implements AnnotationHandler<A, E> {


    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationClass() {
        return (Class<A>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    public Class<E> getElementClass() {
        return (Class<E>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
    }


}
