package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.TypeElement;

@Handler
@Order
public class AnnotationHandlerLog extends AnnotationHandlerBase<Loguno.Logger, TypeElement> {

    public AnnotationHandlerLog(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Logger annotation, TypeElement e, ClassContext classContext) {
        System.out.println("AnnotationHandlerLog: " + annotation.value());
    }



}
