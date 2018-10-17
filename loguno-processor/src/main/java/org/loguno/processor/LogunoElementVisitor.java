package org.loguno.processor;

import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import java.util.Arrays;

public class LogunoElementVisitor extends ElementScanner8<Void, ClassContext> {

    private HandlersProvider handlersProvider;
    private JavacProcessingEnvironment environment;

    LogunoElementVisitor(JavacProcessingEnvironment environment) {
        super();
        this.handlersProvider = HandlersProvider.create(environment);
        this.environment = environment;
    }

    @Override
    public Void visitPackage(PackageElement e, ClassContext recorder) {
        processHandlers(e, recorder);
        return super.visitPackage(e, recorder);
    }

    @Override
    public Void visitType(TypeElement e, ClassContext classContext) {

        classContext.getClasses().addLast(e.getQualifiedName().toString());

        try {
            processHandlers(e, classContext);
            return super.visitType(e, classContext);
        } finally {
            classContext.getClasses().removeLast();
            if (classContext.getLoggers().size() > 0)
                classContext.getLoggers().removeLast();
        }
    }

    @Override
    public Void visitVariable(VariableElement e, ClassContext recorder) {
        // catches methods arguments only
        processHandlers(e, recorder);
        return super.visitVariable(e, recorder);
    }

    @Override
    public Void visitExecutable(ExecutableElement e, ClassContext classContext) {

        classContext.getMethods().addLast(e.getSimpleName().toString());

        try {
            processHandlers(e, classContext);
            Trees trees = Trees.instance(environment);
            MethodTree method = trees.getTree(e);

            method.accept(new LogunoMethodVisitor(handlersProvider), classContext);

            method.getBody().accept(new LogunoMethodBodyVisitor(handlersProvider), classContext);

            return super.visitExecutable(e, classContext);
        } finally {
            classContext.getMethods().removeLast();
        }
    }

    @Override
    public Void visitTypeParameter(TypeParameterElement e, ClassContext recorder) {
        // catches class fields
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
