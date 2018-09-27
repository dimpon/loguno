package org.loguno.processor.handlers;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

public interface AnnotationHandler<A extends Annotation, E extends Element> {

    default void process(Annotation annotation, E element, ClassContext classContext) {
        processTree(getAnnotationClass().cast(annotation), element, classContext);
    }

    void processTree(A annotation, E element, ClassContext classContext);

    Class<A> getAnnotationClass();

    Class<E> getElementClass();
}
