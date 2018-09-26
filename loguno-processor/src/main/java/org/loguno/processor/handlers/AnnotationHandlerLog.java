package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Order
public class AnnotationHandlerLog implements AnnotationHandler<Loguno.Log, TypeElement> {
    @Override
    public void processTree(Loguno.Log annotation, TypeElement e, JavacProcessingEnvironment env) {
        System.out.println("AnnotationHandlerLog: " + annotation.value());
    }

    @Override
    public Class<Loguno.Log> getAnnotationClass() {
        return Loguno.Log.class;
    }

    @Override
    public Class<TypeElement> getElementClass() {
        return TypeElement.class;
    }

}
