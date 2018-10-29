package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.*;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.lang.Void;
import java.util.*;

import static org.loguno.processor.handlers.AnnotationHandlerPipedExceptionsCatch.*;
import static org.loguno.processor.utils.JCTreeUtils.*;

/**
 * @author Dmitrii Ponomarev
 */
@AllArgsConstructor
public class LogunoMethodVisitor extends TreeScanner<Void, ClassContext> {

    // private final JavacProcessingEnvironment environment;
    private final HandlersProvider handlersProvider;



    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {
        //sym.owner = MethodSymbol
        List<? extends AnnotationTree> variableAnnotations = variableTree.getModifiers().getAnnotations();
        variableAnnotations.forEach(annotation -> {
            findHandlersAndCall(annotation, variableTree, classContext);
        });
        return super.visitVariable(variableTree, classContext);
    }


    @Override
    public Void visitMethod(MethodTree methodTree, ClassContext classContext) {
        if (methodTree.getBody() != null)
            JCTreeUtils.findVoidHandlersAndCall(methodTree, classContext);
        return this.visitMethodWithoutParams(methodTree, classContext);
    }

    @Override
    public Void visitThrow(ThrowTree var1, ClassContext var2) {
        return super.visitThrow(var1, var2);
    }


    public Void visitMethodWithoutParams(MethodTree methodTree, ClassContext classContext) {
        Void var3 = this.scan(methodTree.getModifiers(), classContext);
        //var3 = this.scanAndReduce(methodTree.getReturnType(), classContext, var3);
        //var3 = this.scanAndReduce(methodTree.getTypeParameters(), classContext, var3);
        //var3 = this.scanAndReduce(methodTree.getParameters(), classContext, var3);
        //var3 = this.scanAndReduce(methodTree.getReceiverParameter(), classContext, var3);
        var3 = this.scanAndReduce(methodTree.getThrows(), classContext, var3);
        var3 = this.scanAndReduce(methodTree.getBody(), classContext, var3);
        //var3 = this.scanAndReduce(methodTree.getDefaultValue(), classContext, var3);
        return var3;
    }

    @Override
    public Void visitReturn(ReturnTree var1, ClassContext var2) {
        return super.visitReturn(var1, var2);
    }


    @Override
    public Void visitCatch(CatchTree catchBlock, ClassContext classContext) {

        Tree.Kind kind = catchBlock.getParameter().getType().getKind();

        //normal catch
        if (kind == Tree.Kind.IDENTIFIER) {
            List<? extends AnnotationTree> variableAnnotations = catchBlock.getParameter().getModifiers().getAnnotations();
            variableAnnotations.forEach(annotation -> {
                findHandlersAndCall(annotation, catchBlock, classContext);
            });
        }

        //catch with piped exceptions
        if (kind == Tree.Kind.UNION_TYPE) {
            com.sun.tools.javac.util.List<JCTree.JCExpression> alternatives = ((JCTree.JCTypeUnion) catchBlock.getParameter().getType())
                    .getTypeAlternatives();

            Name e = catchBlock.getParameter().getName();
            // annotation of the first exception is places separately, like annotations of whole exceptions group
            List<? extends AnnotationTree> annotations_1 = catchBlock.getParameter().getModifiers().getAnnotations();
            JCTree.JCExpression exceptionClass_1 = alternatives.get(0);

            final PipedExceptionsHolder pipedExceptions = PipedExceptionsHolder.of()
                    .varName(e.toString())
                    .element(catchBlock)
                    .putIfAbsentOneException(exceptionClass_1, annotations_1);

            alternatives.forEach(exception -> {
                if (exception instanceof JCTree.JCAnnotatedType) {
                    JCTree.JCAnnotatedType annotated = (JCTree.JCAnnotatedType) exception;
                    pipedExceptions.putIfAbsentOneException(annotated.underlyingType, annotated.getAnnotations());
                }
            });

            findVoidHandlersAndCall(pipedExceptions, classContext);
        }

        return this.visitCatchWithoutParam(catchBlock, classContext);
    }

    public Void visitCatchWithoutParam(CatchTree var1, ClassContext var2) {
        //Object var3 = this.scan((Tree)var1.getParameter(), var2);
        Void var3 = this.scan(var1.getBlock(), var2);
        return var3;
    }

    @Override
    public Void visitBlock(BlockTree block, ClassContext classContext) {

        try {
            classContext.getBlocks().add(block);
            return super.visitBlock(block, classContext);
        } finally {
            classContext.getBlocks().removeLast();
        }
    }


    private Void scanAndReduce(Tree var1, ClassContext var2, Void var3) {
        return this.reduce(this.scan(var1, var2), var3);
    }

    private Void scanAndReduce(Iterable<? extends Tree> var1, ClassContext var2, Void var3) {
        return this.reduce(this.scan(var1, var2), var3);
    }
}
