package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.*;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.Name;
import java.lang.Void;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Stream;
import static org.loguno.processor.handlers.AnnotationHandlerMultipleExceptionsCatch2.*;
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

			final PipedExceptions pipedExceptions = PipedExceptions.of()
					.varName(e.toString())
					.element(catchBlock)
					.putIfAbsentOneException(exceptionClass_1, annotations_1);

			alternatives.forEach(exception -> {
				if (exception instanceof JCTree.JCAnnotatedType) {
					JCTree.JCAnnotatedType annotated = (JCTree.JCAnnotatedType) exception;
					JCTree.JCExpression exceptionClass = annotated.underlyingType;
					com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations = annotated.getAnnotations();
					pipedExceptions.putIfAbsentOneException(exceptionClass, annotations);
				}
			});

			findHandlersAndCall(pipedExceptions, classContext);

			/*
			 * annotations1.forEach(annotation -> {
			 * findHandlersAndCall(annotation, AnnotationHandlerMultipleExceptionsCatch
			 * .ExceptionTO.of(e, (JCTree.JCBlock) block, exceptionClass1, (JCTree) catchBlock), classContext);
			 * });
			 */

			/*
			 * for (int i = 1; i < typeAlternatives.size(); i++) {
			 * 
			 * JCTree.JCExpression exception = typeAlternatives.get(i);
			 * if (exception instanceof JCTree.JCAnnotatedType) {
			 * JCTree.JCAnnotatedType annException = (JCTree.JCAnnotatedType) exception;
			 * 
			 * JCTree.JCExpression exceptionClass = annException.underlyingType;
			 * 
			 * com.sun.tools.javac.util.List<JCTree.JCAnnotation> annotations = annException.getAnnotations();
			 * 
			 * for (JCTree.JCAnnotation annotation : annotations) {
			 * findHandlersAndCall(annotation,
			 * AnnotationHandlerMultipleExceptionsCatch.ExceptionTO.of(e, (JCTree.JCBlock) block, exceptionClass, (JCTree) catchBlock),
			 * classContext);
			 * }
			 * 
			 * }
			 * }
			 */
		}

		return super.visitCatch(catchBlock, classContext);
	}

	@Override
	public Void visitBlock(BlockTree block, ClassContext classContext) {

		/*
		 * Class<org.loguno.Loguno> aClass = (Class<org.loguno.Loguno>) this.getClass().getClassLoader().loadClass("org.loguno.Loguno");
		 * 
		 * Map<String, Object> values = new HashMap<>();
		 * values.put("value", "some");
		 * 
		 * Loguno annotationInstance = createAnnotationInstance(values, aClass);
		 * 
		 * Stream<? extends AnnotationHandler<?, BlockTree>> handlersByElementAndAnnotation = handlersProvider.getHandlersByElementAndAnnotation(aClass, block);
		 * 
		 * handlersByElementAndAnnotation.forEach(o -> o.process(annotationInstance, block, classContext));
		 */

		try {
			classContext.getBlocks().add(block);
			return super.visitBlock(block, classContext);
		} finally {
			classContext.getBlocks().removeLast();
		}
	}
}
