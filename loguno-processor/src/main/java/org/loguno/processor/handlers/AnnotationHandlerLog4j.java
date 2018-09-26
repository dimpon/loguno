package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Order
public class AnnotationHandlerLog4j implements AnnotationHandler<Loguno.Log4j,TypeElement> {
    @Override
    public void processTree(Loguno.Log4j annotation, TypeElement e, JavacProcessingEnvironment env) {
        System.out.println("AnnotationHandlerLog4j: " + annotation.value());
    }

    @Override
    public Class<Loguno.Log4j> getAnnotationClass() {
        return Loguno.Log4j.class;
    }

    @Override
    public Class<TypeElement> getElementClass() {
        return TypeElement.class;
    }

}
