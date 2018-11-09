package org.loguno.processor;

//import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.annotations.AnnotationRetriever;

import javax.lang.model.element.ElementKind;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.loguno.processor.utils.JCTreeUtils.VOID_ANN;

@AllArgsConstructor
public class LogunoScanner extends TreeScanner {

    final private ClassContext classContext;

    private AnnotationRetriever annotationRetriever;

    @Override
    public void visitTopLevel(JCTree.JCCompilationUnit jcCompilationUnit) {
        // visit package
    }

    @Override
    public void visitClassDef(final JCTree.JCClassDecl jcClassDecl) {
        List<Annotation> annotations = annotationRetriever.getTreeAnnotations(jcClassDecl.getModifiers()).collect(Collectors.toList());
        findHandlersBeforeAndExecute(annotations, jcClassDecl);
        super.visitClassDef(jcClassDecl);
        findHandlersAfterAndExecute(annotations, jcClassDecl);
    }


    @Override
    public void visitMethodDef(JCTree.JCMethodDecl jcMethodDecl) {
        List<Annotation> annotations = annotationRetriever.getTreeAnnotations(jcMethodDecl.getModifiers()).collect(Collectors.toList());
        findHandlersBeforeAndExecute(annotations, jcMethodDecl);
        super.visitMethodDef(jcMethodDecl);
        findHandlersAfterAndExecute(annotations, jcMethodDecl);
    }

    @Override
    public void visitVarDef(JCTree.JCVariableDecl jcVariableDecl) {
        //class field

        ClassContext.VarZone last = classContext.getWhereIam().getLast();

        switch (last){
            case CLASS:
                break;
            case METHOD:
                visitMethodParam(jcVariableDecl);
                break;
            case BLOCK:
                visitLocalVariable(jcVariableDecl);
                break;

        }

    }

    private void visitMethodParam(JCTree.JCVariableDecl jcVariableDecl){
        List<Annotation> annotations = annotationRetriever.getTreeAnnotations(jcVariableDecl.getModifiers()).collect(Collectors.toList());
        findHandlersBeforeAndExecute(annotations, jcVariableDecl);
        super.visitVarDef(jcVariableDecl);
        findHandlersAfterAndExecute(annotations, jcVariableDecl);
    }

    private void visitLocalVariable(JCTree.JCVariableDecl jcVariableDecl){
        super.visitVarDef(jcVariableDecl);
    }

    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        findHandlersBeforeAndExecute(Collections.emptyList(), jcBlock);
        super.visitBlock(jcBlock);
        findHandlersAfterAndExecute(Collections.emptyList(), jcBlock);
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

        HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(VOID_ANN.annotationType(), e)
                .forEach(h -> h.process(VOID_ANN, e, classContext));

        annotations.forEach(ann -> {
            HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), e)
                    .forEach(h -> h.process(ann, e, classContext));
        });
    }

    private void findHandlersAfterAndExecute(List<Annotation> annotations, Object e) {

        annotations.forEach(ann -> {
            HandlersProvider.instance().getHandlersAfterByElementAndAnnotation(ann.annotationType(), e)
                    .forEach(h -> h.process(ann, e, classContext));
        });

        HandlersProvider.instance().getHandlersAfterByElementAndAnnotation(VOID_ANN.annotationType(), e)
                .forEach(h -> h.process(VOID_ANN, e, classContext));
    }

}
