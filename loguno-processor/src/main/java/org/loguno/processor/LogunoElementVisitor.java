package org.loguno.processor;

import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import java.util.Arrays;

public class LogunoElementVisitor extends ElementScanner8<ClassContext, ClassContext> {

    private HandlersProvider handlersProvider;

    LogunoElementVisitor(JavacProcessingEnvironment environment) {
        super();
        handlersProvider = HandlersProvider.create(environment);
    }

    @Override
    public ClassContext visitPackage(PackageElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitPackage(e, recorder);
    }

    @Override
    public ClassContext visitType(TypeElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitType(e, recorder);
    }

    @Override
    public ClassContext visitVariable(VariableElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitVariable(e, recorder);
    }

    @Override
    public ClassContext visitExecutable(ExecutableElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitExecutable(e, recorder);
    }

    @Override
    public ClassContext visitTypeParameter(TypeParameterElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitTypeParameter(e, recorder);
    }


    private <T extends Element> void processHandlers(T e, ClassContext recorder) {

        handlersProvider
                .supportedAnnotations()
                .forEach(logunoAnnClass -> {
                    Arrays.stream(e.getAnnotationsByType(logunoAnnClass))
                            .forEach(annFromElement -> {
                                handlersProvider
                                        .getHandlersByElementAndAnnotation(logunoAnnClass, e)
                                        .forEach(handler -> handler.process(annFromElement, e, recorder));
                            });
                });
    }
}
