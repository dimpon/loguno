package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.*;

import javax.lang.model.element.Name;
import java.lang.Void;
import java.util.*;

import static org.loguno.processor.handlers.AnnotationHandlerPipedExceptionsCatch.*;
import static org.loguno.processor.utils.JCTreeUtils.*;

/**
 * @author Dmitrii Ponomarev
 */
@AllArgsConstructor
public class LogunoMethodBodyVisitor extends TreeScanner<Void, ClassContext> {

	// private final JavacProcessingEnvironment environment;
	private final HandlersProvider handlersProvider;

	@Override
	public Void visitVariable(VariableTree variableTree, ClassContext classContext) {

		List<? extends AnnotationTree> variableAnnotations = variableTree.getModifiers().getAnnotations();
		variableAnnotations.forEach(annotation -> {
			findHandlersAndCall(annotation, variableTree, classContext);
		});
		return super.visitVariable(variableTree, classContext);
	}



	@Override
	public Void visitCatch(CatchTree catchBlock, ClassContext classContext) {

		Tree.Kind kind = catchBlock.getParameter().getType().getKind();

		if (kind == Tree.Kind.IDENTIFIER) {
			List<? extends AnnotationTree> variableAnnotations = catchBlock.getParameter().getModifiers().getAnnotations();
			variableAnnotations.forEach(annotation -> {
				findHandlersAndCall(annotation, catchBlock, classContext);
			});
		}


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

		return super.visitCatch(catchBlock, classContext);
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
}
