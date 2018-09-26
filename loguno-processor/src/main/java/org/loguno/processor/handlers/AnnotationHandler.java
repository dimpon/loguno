package org.loguno.processor.handlers;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;

public interface AnnotationHandler<A extends Annotation, E extends Element> {

    default void process(Annotation annotation, E element, ActionsRecorder context) {
        processTree(getAnnotationClass().cast(annotation), element, context);
    }

    void processTree(A annotation, E element, ActionsRecorder recorder);

    Class<A> getAnnotationClass();

    Class<E> getElementClass();
}
