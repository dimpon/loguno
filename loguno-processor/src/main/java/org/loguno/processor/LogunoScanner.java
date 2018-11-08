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
        this.visitClassDefFurther(jcClassDecl);
        findHandlersAfterAndExecute(annotations, jcClassDecl);
    }

    private void visitClassDefFurther(JCTree.JCClassDecl var1) {
        //this.scan((JCTree)var1.mods);
        //this.scan(var1.typarams);
        //this.scan((JCTree)var1.extending);
        //this.scan(var1.implementing);
        super.scan(var1.defs);
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
        if(jcVariableDecl.sym!=null && jcVariableDecl.sym.getKind()==ElementKind.FIELD)
            return;


        //super.visitVarDef(jcVariableDecl);
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
        annotations.add(VOID_ANN);
        annotations.forEach(ann -> {
            Stream<? extends AnnotationHandler<?, Object>> handlers = HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), e);
            List<? extends AnnotationHandler<?, Object>> collect = handlers.collect(Collectors.toList());
            collect.forEach(h -> {
                h.process(ann, e, classContext);
            });
        });
    }

    private void findHandlersAfterAndExecute(List<Annotation> annotations, Object e) {
        annotations.add(VOID_ANN);
        annotations.forEach(ann -> {
            Stream<? extends AnnotationHandler<?, Object>> handlers = HandlersProvider.instance().getHandlersAfterByElementAndAnnotation(ann.annotationType(), e);
            List<? extends AnnotationHandler<?, Object>> collect = handlers.collect(Collectors.toList());
            collect.forEach(h -> {
                h.process(ann, e, classContext);
            });
        });
    }

}
