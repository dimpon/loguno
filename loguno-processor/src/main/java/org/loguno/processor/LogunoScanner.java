package org.loguno.processor;

//import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeInfo;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.List;

public class LogunoScanner extends TreeScanner {
    @Override
    public void scan(JCTree jcTree) {
        super.scan(jcTree);
    }

    @Override
    public void scan(List<? extends JCTree> list) {
        super.scan(list);
    }

    @Override
    public void visitTopLevel(JCTree.JCCompilationUnit jcCompilationUnit) {
        super.visitTopLevel(jcCompilationUnit);
    }

    @Override
    public void visitImport(JCTree.JCImport jcImport) {
        super.visitImport(jcImport);
    }

    @Override
    public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
        super.visitClassDef(jcClassDecl);
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
    public void visitSkip(JCTree.JCSkip jcSkip) {
        super.visitSkip(jcSkip);
    }

    @Override
    public void visitBlock(JCTree.JCBlock jcBlock) {
        super.visitBlock(jcBlock);
    }

    @Override
    public void visitDoLoop(JCTree.JCDoWhileLoop jcDoWhileLoop) {
        super.visitDoLoop(jcDoWhileLoop);
    }

    @Override
    public void visitWhileLoop(JCTree.JCWhileLoop jcWhileLoop) {
        super.visitWhileLoop(jcWhileLoop);
    }

    @Override
    public void visitForLoop(JCTree.JCForLoop jcForLoop) {
        super.visitForLoop(jcForLoop);
    }

    @Override
    public void visitForeachLoop(JCTree.JCEnhancedForLoop jcEnhancedForLoop) {
        super.visitForeachLoop(jcEnhancedForLoop);
    }

    @Override
    public void visitLabelled(JCTree.JCLabeledStatement jcLabeledStatement) {
        super.visitLabelled(jcLabeledStatement);
    }

    @Override
    public void visitSwitch(JCTree.JCSwitch jcSwitch) {
        super.visitSwitch(jcSwitch);
    }

    @Override
    public void visitCase(JCTree.JCCase jcCase) {
        super.visitCase(jcCase);
    }

    @Override
    public void visitSynchronized(JCTree.JCSynchronized jcSynchronized) {
        super.visitSynchronized(jcSynchronized);
    }

    @Override
    public void visitTry(JCTree.JCTry jcTry) {
        super.visitTry(jcTry);
    }

    @Override
    public void visitCatch(JCTree.JCCatch jcCatch) {
        super.visitCatch(jcCatch);
    }

    @Override
    public void visitConditional(JCTree.JCConditional jcConditional) {
        super.visitConditional(jcConditional);
    }

    @Override
    public void visitIf(JCTree.JCIf jcIf) {
        super.visitIf(jcIf);
    }

    @Override
    public void visitExec(JCTree.JCExpressionStatement jcExpressionStatement) {
        super.visitExec(jcExpressionStatement);
    }

    @Override
    public void visitBreak(JCTree.JCBreak jcBreak) {
        super.visitBreak(jcBreak);
    }

    @Override
    public void visitContinue(JCTree.JCContinue jcContinue) {
        super.visitContinue(jcContinue);
    }

    @Override
    public void visitReturn(JCTree.JCReturn jcReturn) {
        super.visitReturn(jcReturn);
    }

    @Override
    public void visitThrow(JCTree.JCThrow jcThrow) {
        super.visitThrow(jcThrow);
    }

    @Override
    public void visitAssert(JCTree.JCAssert jcAssert) {
        super.visitAssert(jcAssert);
    }

    @Override
    public void visitApply(JCTree.JCMethodInvocation jcMethodInvocation) {
        super.visitApply(jcMethodInvocation);
    }

    @Override
    public void visitNewClass(JCTree.JCNewClass jcNewClass) {
        super.visitNewClass(jcNewClass);
    }

    @Override
    public void visitNewArray(JCTree.JCNewArray jcNewArray) {
        super.visitNewArray(jcNewArray);
    }

    @Override
    public void visitLambda(JCTree.JCLambda jcLambda) {
        super.visitLambda(jcLambda);
    }

    @Override
    public void visitParens(JCTree.JCParens jcParens) {
        super.visitParens(jcParens);
    }

    @Override
    public void visitAssign(JCTree.JCAssign jcAssign) {
        super.visitAssign(jcAssign);
    }

    @Override
    public void visitAssignop(JCTree.JCAssignOp jcAssignOp) {
        super.visitAssignop(jcAssignOp);
    }

    @Override
    public void visitUnary(JCTree.JCUnary jcUnary) {
        super.visitUnary(jcUnary);
    }

    @Override
    public void visitBinary(JCTree.JCBinary jcBinary) {
        super.visitBinary(jcBinary);
    }

    @Override
    public void visitTypeCast(JCTree.JCTypeCast jcTypeCast) {
        super.visitTypeCast(jcTypeCast);
    }

    @Override
    public void visitTypeTest(JCTree.JCInstanceOf jcInstanceOf) {
        super.visitTypeTest(jcInstanceOf);
    }

    @Override
    public void visitIndexed(JCTree.JCArrayAccess jcArrayAccess) {
        super.visitIndexed(jcArrayAccess);
    }

    @Override
    public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
        super.visitSelect(jcFieldAccess);
    }

    @Override
    public void visitReference(JCTree.JCMemberReference jcMemberReference) {
        super.visitReference(jcMemberReference);
    }

    @Override
    public void visitIdent(JCTree.JCIdent jcIdent) {
        super.visitIdent(jcIdent);
    }

    @Override
    public void visitLiteral(JCTree.JCLiteral jcLiteral) {
        super.visitLiteral(jcLiteral);
    }

    @Override
    public void visitTypeIdent(JCTree.JCPrimitiveTypeTree jcPrimitiveTypeTree) {
        super.visitTypeIdent(jcPrimitiveTypeTree);
    }

    @Override
    public void visitTypeArray(JCTree.JCArrayTypeTree jcArrayTypeTree) {
        super.visitTypeArray(jcArrayTypeTree);
    }

    @Override
    public void visitTypeApply(JCTree.JCTypeApply jcTypeApply) {
        super.visitTypeApply(jcTypeApply);
    }

    @Override
    public void visitTypeUnion(JCTree.JCTypeUnion jcTypeUnion) {
        super.visitTypeUnion(jcTypeUnion);
    }

    @Override
    public void visitTypeIntersection(JCTree.JCTypeIntersection jcTypeIntersection) {
        super.visitTypeIntersection(jcTypeIntersection);
    }

    @Override
    public void visitTypeParameter(JCTree.JCTypeParameter jcTypeParameter) {
        super.visitTypeParameter(jcTypeParameter);
    }

    @Override
    public void visitWildcard(JCTree.JCWildcard jcWildcard) {
        super.visitWildcard(jcWildcard);
    }

    @Override
    public void visitTypeBoundKind(JCTree.TypeBoundKind typeBoundKind) {
        super.visitTypeBoundKind(typeBoundKind);
    }

    @Override
    public void visitModifiers(JCTree.JCModifiers jcModifiers) {
        super.visitModifiers(jcModifiers);
    }

    @Override
    public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
        super.visitAnnotation(jcAnnotation);
    }

    @Override
    public void visitAnnotatedType(JCTree.JCAnnotatedType jcAnnotatedType) {
        super.visitAnnotatedType(jcAnnotatedType);
    }

    @Override
    public void visitErroneous(JCTree.JCErroneous jcErroneous) {
        super.visitErroneous(jcErroneous);
    }

    @Override
    public void visitLetExpr(JCTree.LetExpr letExpr) {
        super.visitLetExpr(letExpr);
    }

    @Override
    public void visitTree(JCTree jcTree) {
        super.visitTree(jcTree);
    }
}
