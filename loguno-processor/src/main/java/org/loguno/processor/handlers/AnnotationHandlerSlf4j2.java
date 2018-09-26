package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import org.loguno.Loguno;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Ponomarev
 */
@Order(1)
public class AnnotationHandlerSlf4j2 implements AnnotationHandler<Loguno.Slf4j, TypeElement> {

    @Override
    public void processTree(Loguno.Slf4j annotation, TypeElement typeElement, JavacProcessingEnvironment env) {

        System.out.println("AnnotationHandlerSlf4j2: " + annotation.value());

    }

    @Override
    public Class<Loguno.Slf4j> getAnnotationClass() {
        return Loguno.Slf4j.class;
    }

    @Override
    public Class<TypeElement> getElementClass() {
        return TypeElement.class;
    }
}
