package org.loguno.processor;

//import com.sun.source.util.TreeScanner;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.JCTreeUtils;
import org.loguno.processor.utils.annotations.AnnotationRetriever;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

        JCTree owner = classContext.getVarOwner(jcVariableDecl);

        if (owner instanceof JCTree.JCBlock) {
            visitBlockVariable(jcVariableDecl, super::visitVarDef);
        } else if(owner instanceof JCTree.JCClassDecl) {
            super.visitVarDef(jcVariableDecl);
        } else if(JCTreeUtils.hasBody(owner)) {
            visitParenthesesVariable(jcVariableDecl, super::visitVarDef);
        }else {
            super.visitVarDef(jcVariableDecl);
        }
    }

    private void visitParenthesesVariable(JCTree.JCVariableDecl variable, Consumer<JCTree.JCVariableDecl> scanFurther) {

        JCTree owner = classContext.getVarOwner(variable);

        JCTree.JCStatement body = JCTreeUtils.getBody(owner);
        VarInParentheses block = VarInParentheses.of().parentheses(owner).variable(variable).block(body);

        List<Annotation> annotations = annotationRetriever.getTreeAnnotations(variable.getModifiers()).collect(Collectors.toList());
        findHandlersBeforeAndExecute(annotations, block);
        scanFurther.accept(variable);
        findHandlersAfterAndExecute(annotations, block);
    }

    private void visitBlockVariable(JCTree.JCVariableDecl variable, Consumer<JCTree.JCVariableDecl> scanFurther) {
        JCTree owner = classContext.getVarOwner(variable);
        VarInBlock block = VarInBlock.of().variable(variable).block((JCTree.JCBlock) owner);

        List<Annotation> annotations = annotationRetriever.getTreeAnnotations(variable.getModifiers()).collect(Collectors.toList());

        findHandlersBeforeAndExecute(annotations, block);
        scanFurther.accept(variable);
        findHandlersAfterAndExecute(annotations, block);

    }

    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
    }

    @Override
    public void visitLambda(JCTree.JCLambda var1) {
        super.visitLambda(var1);
    }

    @Override
    public void visitLabelled(JCTree.JCLabeledStatement jcLabeledStatement) {
        super.visitLabelled(jcLabeledStatement);
    }

    @Override
    public void visitCatch(JCTree.JCCatch jcCatch) {
        super.visitCatch(jcCatch);
    }

    @Override
    public void scan(JCTree tree) {
        classContext.getBreadcrumb().addLast(tree);
        super.scan(tree);
        classContext.getBreadcrumb().removeLast();
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

    @NoArgsConstructor(staticName = "of")
    @Setter
    @Getter
    @Accessors(fluent = true, chain = true)
    public static class VarInBlock {
        JCTree.JCVariableDecl variable;
        JCTree.JCBlock block;
    }

    @NoArgsConstructor(staticName = "of")
    @Setter
    @Getter
    @Accessors(fluent = true, chain = true)
    public static class VarInParentheses {
        JCTree.JCVariableDecl variable;
        JCTree parentheses;
        JCTree.JCStatement block;
    }

}
