package org.loguno.processor;

import org.loguno.Loguno;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.HandlersProvider;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LogunoElementVisitor extends ElementScanner8<Void, JavacProcessingEnvironment> {

    private HandlersProvider handlersProvider = HandlersProvider.create();

    public LogunoElementVisitor() {
        super();
    }

    @Override
    public Void visitPackage(PackageElement e, JavacProcessingEnvironment env) {
        processHandlers(e, env);
        return super.visitPackage(e, env);
    }

    @Override
    public Void visitType(TypeElement e, JavacProcessingEnvironment env) {
        processHandlers(e, env);
        return super.visitType(e, env);
    }

    @Override
    public Void visitVariable(VariableElement e, JavacProcessingEnvironment env) {
        processHandlers(e, env);
        return super.visitVariable(e, env);
    }

    @Override
    public Void visitExecutable(ExecutableElement e, JavacProcessingEnvironment env) {
        processHandlers(e, env);
        return super.visitExecutable(e, env);
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, JavacProcessingEnvironment env) {
        processHandlers(e, env);
        return super.visitTypeParameter(e, env);
    }


    private <T extends Element> void processHandlers(T e, JavacProcessingEnvironment env) {

        handlersProvider
                .supportedAnnotations()
                .forEach(annClass -> {
                    Arrays.stream(e.getAnnotationsByType(annClass))
                            .forEach(ann -> {
                                handlersProvider
                                        .getHandlersByElementAndAnnotation(annClass, e)
                                        .forEach(handler -> handler.process(ann, e, env));
                            });
                });
    }
}
