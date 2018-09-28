package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;

/**
 * @author Dmitrii Ponomarev
 */
public class LogunoTreeVisitor extends TreeScanner<Void,Void> {
    public LogunoTreeVisitor() {
        super();
    }

    @Override
    public Void scan(Tree tree, Void aVoid) {
        return super.scan(tree, aVoid);
    }

    @Override
    public Void scan(Iterable<? extends Tree> iterable, Void aVoid) {
        return super.scan(iterable, aVoid);
    }

    @Override
    public Void reduce(Void aVoid, Void r1) {
        return super.reduce(aVoid, r1);
    }

    @Override
    public Void visitCompilationUnit(CompilationUnitTree compilationUnitTree, Void aVoid) {
        return super.visitCompilationUnit(compilationUnitTree, aVoid);
    }

    @Override
    public Void visitImport(ImportTree importTree, Void aVoid) {
        return super.visitImport(importTree, aVoid);
    }

    @Override
    public Void visitClass(ClassTree classTree, Void aVoid) {
        return super.visitClass(classTree, aVoid);
    }

    @Override
    public Void visitMethod(MethodTree methodTree, Void aVoid) {
        return super.visitMethod(methodTree, aVoid);
    }

    @Override
    public Void visitVariable(VariableTree variableTree, Void aVoid) {
        return super.visitVariable(variableTree, aVoid);
    }

    @Override
    public Void visitEmptyStatement(EmptyStatementTree emptyStatementTree, Void aVoid) {
        return super.visitEmptyStatement(emptyStatementTree, aVoid);
    }

    @Override
    public Void visitBlock(BlockTree blockTree, Void aVoid) {
        return super.visitBlock(blockTree, aVoid);
    }

    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree doWhileLoopTree, Void aVoid) {
        return super.visitDoWhileLoop(doWhileLoopTree, aVoid);
    }

    @Override
    public Void visitWhileLoop(WhileLoopTree whileLoopTree, Void aVoid) {
        return super.visitWhileLoop(whileLoopTree, aVoid);
    }

    @Override
    public Void visitForLoop(ForLoopTree forLoopTree, Void aVoid) {
        return super.visitForLoop(forLoopTree, aVoid);
    }

    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree enhancedForLoopTree, Void aVoid) {
        return super.visitEnhancedForLoop(enhancedForLoopTree, aVoid);
    }

    @Override
    public Void visitLabeledStatement(LabeledStatementTree labeledStatementTree, Void aVoid) {
        return super.visitLabeledStatement(labeledStatementTree, aVoid);
    }

    @Override
    public Void visitSwitch(SwitchTree switchTree, Void aVoid) {
        return super.visitSwitch(switchTree, aVoid);
    }

    @Override
    public Void visitCase(CaseTree caseTree, Void aVoid) {
        return super.visitCase(caseTree, aVoid);
    }

    @Override
    public Void visitSynchronized(SynchronizedTree synchronizedTree, Void aVoid) {
        return super.visitSynchronized(synchronizedTree, aVoid);
    }

    @Override
    public Void visitTry(TryTree tryTree, Void aVoid) {
        return super.visitTry(tryTree, aVoid);
    }

    @Override
    public Void visitCatch(CatchTree catchTree, Void aVoid) {
        return super.visitCatch(catchTree, aVoid);
    }

    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree conditionalExpressionTree, Void aVoid) {
        return super.visitConditionalExpression(conditionalExpressionTree, aVoid);
    }

    @Override
    public Void visitIf(IfTree ifTree, Void aVoid) {
        return super.visitIf(ifTree, aVoid);
    }

    @Override
    public Void visitExpressionStatement(ExpressionStatementTree expressionStatementTree, Void aVoid) {
        return super.visitExpressionStatement(expressionStatementTree, aVoid);
    }

    @Override
    public Void visitBreak(BreakTree breakTree, Void aVoid) {
        return super.visitBreak(breakTree, aVoid);
    }

    @Override
    public Void visitContinue(ContinueTree continueTree, Void aVoid) {
        return super.visitContinue(continueTree, aVoid);
    }

    @Override
    public Void visitReturn(ReturnTree returnTree, Void aVoid) {
        return super.visitReturn(returnTree, aVoid);
    }

    @Override
    public Void visitThrow(ThrowTree throwTree, Void aVoid) {
        return super.visitThrow(throwTree, aVoid);
    }

    @Override
    public Void visitAssert(AssertTree assertTree, Void aVoid) {
        return super.visitAssert(assertTree, aVoid);
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree methodInvocationTree, Void aVoid) {
        return super.visitMethodInvocation(methodInvocationTree, aVoid);
    }

    @Override
    public Void visitNewClass(NewClassTree newClassTree, Void aVoid) {
        return super.visitNewClass(newClassTree, aVoid);
    }

    @Override
    public Void visitNewArray(NewArrayTree newArrayTree, Void aVoid) {
        return super.visitNewArray(newArrayTree, aVoid);
    }

    @Override
    public Void visitLambdaExpression(LambdaExpressionTree lambdaExpressionTree, Void aVoid) {
        return super.visitLambdaExpression(lambdaExpressionTree, aVoid);
    }

    @Override
    public Void visitParenthesized(ParenthesizedTree parenthesizedTree, Void aVoid) {
        return super.visitParenthesized(parenthesizedTree, aVoid);
    }

    @Override
    public Void visitAssignment(AssignmentTree assignmentTree, Void aVoid) {
        return super.visitAssignment(assignmentTree, aVoid);
    }

    @Override
    public Void visitCompoundAssignment(CompoundAssignmentTree compoundAssignmentTree, Void aVoid) {
        return super.visitCompoundAssignment(compoundAssignmentTree, aVoid);
    }

    @Override
    public Void visitUnary(UnaryTree unaryTree, Void aVoid) {
        return super.visitUnary(unaryTree, aVoid);
    }

    @Override
    public Void visitBinary(BinaryTree binaryTree, Void aVoid) {
        return super.visitBinary(binaryTree, aVoid);
    }

    @Override
    public Void visitTypeCast(TypeCastTree typeCastTree, Void aVoid) {
        return super.visitTypeCast(typeCastTree, aVoid);
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree instanceOfTree, Void aVoid) {
        return super.visitInstanceOf(instanceOfTree, aVoid);
    }

    @Override
    public Void visitArrayAccess(ArrayAccessTree arrayAccessTree, Void aVoid) {
        return super.visitArrayAccess(arrayAccessTree, aVoid);
    }

    @Override
    public Void visitMemberSelect(MemberSelectTree memberSelectTree, Void aVoid) {
        return super.visitMemberSelect(memberSelectTree, aVoid);
    }

    @Override
    public Void visitMemberReference(MemberReferenceTree memberReferenceTree, Void aVoid) {
        return super.visitMemberReference(memberReferenceTree, aVoid);
    }

    @Override
    public Void visitIdentifier(IdentifierTree identifierTree, Void aVoid) {
        return super.visitIdentifier(identifierTree, aVoid);
    }

    @Override
    public Void visitLiteral(LiteralTree literalTree, Void aVoid) {
        return super.visitLiteral(literalTree, aVoid);
    }

    @Override
    public Void visitPrimitiveType(PrimitiveTypeTree primitiveTypeTree, Void aVoid) {
        return super.visitPrimitiveType(primitiveTypeTree, aVoid);
    }

    @Override
    public Void visitArrayType(ArrayTypeTree arrayTypeTree, Void aVoid) {
        return super.visitArrayType(arrayTypeTree, aVoid);
    }

    @Override
    public Void visitParameterizedType(ParameterizedTypeTree parameterizedTypeTree, Void aVoid) {
        return super.visitParameterizedType(parameterizedTypeTree, aVoid);
    }

    @Override
    public Void visitUnionType(UnionTypeTree unionTypeTree, Void aVoid) {
        return super.visitUnionType(unionTypeTree, aVoid);
    }

    @Override
    public Void visitIntersectionType(IntersectionTypeTree intersectionTypeTree, Void aVoid) {
        return super.visitIntersectionType(intersectionTypeTree, aVoid);
    }

    @Override
    public Void visitTypeParameter(TypeParameterTree typeParameterTree, Void aVoid) {
        return super.visitTypeParameter(typeParameterTree, aVoid);
    }

    @Override
    public Void visitWildcard(WildcardTree wildcardTree, Void aVoid) {
        return super.visitWildcard(wildcardTree, aVoid);
    }

    @Override
    public Void visitModifiers(ModifiersTree modifiersTree, Void aVoid) {
        return super.visitModifiers(modifiersTree, aVoid);
    }

    @Override
    public Void visitAnnotation(AnnotationTree annotationTree, Void aVoid) {
        return super.visitAnnotation(annotationTree, aVoid);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree annotatedTypeTree, Void aVoid) {
        return super.visitAnnotatedType(annotatedTypeTree, aVoid);
    }

    @Override
    public Void visitOther(Tree tree, Void aVoid) {
        return super.visitOther(tree, aVoid);
    }

    @Override
    public Void visitErroneous(ErroneousTree erroneousTree, Void aVoid) {
        return super.visitErroneous(erroneousTree, aVoid);
    }
}
