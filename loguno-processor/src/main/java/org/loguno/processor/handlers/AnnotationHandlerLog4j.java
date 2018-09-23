package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Handler
public class AnnotationHandlerLog4j extends AnnotationHandlerBase<Loguno.Log4j,TypeElement> {
    @Override
    public void processTree(Loguno.Log4j annotation, TypeElement e, JavacProcessingEnvironment env) {
        System.out.println("AnnotationHandlerLog4j: " + annotation.value());
    }

}
