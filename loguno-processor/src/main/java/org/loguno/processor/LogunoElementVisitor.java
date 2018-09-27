package org.loguno.processor;

import org.loguno.processor.handlers.ActionsRecorder;
import org.loguno.processor.handlers.HandlersProvider;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import java.util.Arrays;

public class LogunoElementVisitor extends ElementScanner8<ActionsRecorder, ActionsRecorder> {

    private HandlersProvider handlersProvider;

    LogunoElementVisitor(JavacProcessingEnvironment environment) {
        super();
        handlersProvider = HandlersProvider.create(environment);
    }

    @Override
    public ActionsRecorder visitPackage(PackageElement e, ActionsRecorder recorder) {
        processHandlers(e, recorder);
        return super.visitPackage(e, recorder);
    }

    @Override
    public ActionsRecorder visitType(TypeElement e, ActionsRecorder recorder) {
        processHandlers(e, recorder);
        return super.visitType(e, recorder);
    }

    @Override
    public ActionsRecorder visitVariable(VariableElement e, ActionsRecorder recorder) {
        processHandlers(e, recorder);
        return super.visitVariable(e, recorder);
    }

    @Override
    public ActionsRecorder visitExecutable(ExecutableElement e, ActionsRecorder recorder) {
        processHandlers(e, recorder);
        return super.visitExecutable(e, recorder);
    }

    @Override
    public ActionsRecorder visitTypeParameter(TypeParameterElement e, ActionsRecorder recorder) {
        processHandlers(e, recorder);
        return super.visitTypeParameter(e, recorder);
    }


    private <T extends Element> void processHandlers(T e, ActionsRecorder recorder) {

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
