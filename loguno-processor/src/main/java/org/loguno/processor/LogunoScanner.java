package org.loguno.processor;

//import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.annotations.AnnotationRetriever;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class LogunoScanner extends TreeScanner {

	final private ClassContext classContext;

	private AnnotationRetriever annotationRetriever;

	// visit package
	@Override
	public void visitTopLevel(JCTree.JCCompilationUnit jcCompilationUnit) {

	}

	@Override
	public void visitClassDef(final JCTree.JCClassDecl jcClassDecl) {
		List<Annotation> annotations = annotationRetriever.getTreeAnnotations(jcClassDecl.getModifiers()).collect(Collectors.toList());
		findHandlersBeforeAndExecute(annotations,jcClassDecl);
		super.visitClassDef(jcClassDecl);
        findHandlersAfterAndExecute(annotations,jcClassDecl);
	}

	@Override
	public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
		super.visitMethodDef(jcMethodDecl);
	}

	@Override
	public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
		super.visitVarDef(jcVariableDecl);
	}

	@Override
	public void visitBlock(JCTree.JCBlock jcBlock) {
		super.visitBlock(jcBlock);
	}

	@Override
	public void visitLabelled(JCTree.JCLabeledStatement jcLabeledStatement) {
		super.visitLabelled(jcLabeledStatement);
	}

	@Override
	public void visitCatch(JCTree.JCCatch jcCatch) {
		super.visitCatch(jcCatch);
	}

	private void findHandlersBeforeAndExecute(List<Annotation> annotations, Object e) {
		annotations.forEach(ann -> {
			Stream<? extends AnnotationHandler<?, Object>> handlers = HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), e);
			handlers.forEach(h -> {
				h.process(ann, e, classContext);
			});
		});
	}

	private void findHandlersAfterAndExecute(List<Annotation> annotations, Object e) {
		annotations.forEach(ann -> {
			Stream<? extends AnnotationHandler<?, Object>> handlers = HandlersProvider.instance().getHandlersAfterByElementAndAnnotation(ann.annotationType(), e);
			handlers.forEach(h -> {
				h.process(ann, e, classContext);
			});
		});
	}

}
