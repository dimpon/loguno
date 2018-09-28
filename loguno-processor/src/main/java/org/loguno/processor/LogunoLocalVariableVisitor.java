package org.loguno.processor;

import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.processor.handlers.ClassContext;

/**
 * @author Dmitrii Ponomarev
 */
public class LogunoLocalVariableVisitor extends TreeScanner<Void, ClassContext> {
    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext context) {

        JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl) variableTree;

        if(variable.sym == null){

            List<JCTree.JCAnnotation> annotations = variable.getModifiers().getAnnotations();



        }

        return super.visitVariable(variableTree, context);
    }
}
