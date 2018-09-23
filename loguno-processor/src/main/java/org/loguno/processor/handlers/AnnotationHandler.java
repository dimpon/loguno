package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;


public interface AnnotationHandler<A, E> {

    void processTree(A annotation, E element, JavacProcessingEnvironment env);

    Class<A> getAnnotationClass();

    Class<E> getElementClass();
}
