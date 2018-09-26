package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;


public interface AnnotationHandler<A extends Annotation, E extends Element> {

    default void process(Annotation annotation, E element, JavacProcessingEnvironment env) {
        processTree(getAnnotationClass().cast(annotation), element, env);
    }

    void processTree(A annotation, E element, JavacProcessingEnvironment env);

    Class<A> getAnnotationClass();

    Class<E> getElementClass();
}
