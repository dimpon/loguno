package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import org.loguno.Loguno;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Ponomarev
 */
@Order(1)
public class AnnotationHandlerSlf4j2 extends AnnotationHandlerBase<Loguno.Slf4j, TypeElement> {

    public AnnotationHandlerSlf4j2(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Slf4j annotation, TypeElement typeElement, ActionsRecorder recorder) {

        System.out.println("AnnotationHandlerSlf4j2: " + annotation.value());

    }


}
