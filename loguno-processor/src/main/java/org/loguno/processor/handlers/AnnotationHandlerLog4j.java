package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Order
public class AnnotationHandlerLog4j extends AnnotationHandlerBase<Loguno.Log4j,TypeElement> {

    public AnnotationHandlerLog4j(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Log4j annotation, TypeElement e, ClassContext classContext) {
        System.out.println("AnnotationHandlerLog4j: " + annotation.value());
    }



}
