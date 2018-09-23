package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Handler
public class AnnotationHandlerLog extends AnnotationHandlerBase<Loguno.Log, TypeElement> {
    @Override
    public void processTree(Loguno.Log annotation, TypeElement e, JavacProcessingEnvironment env) {
        System.out.println("AnnotationHandlerLog: " + annotation.value());
    }

}
