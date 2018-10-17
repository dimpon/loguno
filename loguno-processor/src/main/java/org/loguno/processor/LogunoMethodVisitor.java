package org.loguno.processor;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreeScanner;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.utils.JCTreeUtils;

@AllArgsConstructor
public class LogunoMethodVisitor extends TreeScanner<Void, ClassContext> {

    // private final JavacProcessingEnvironment environment;
    private final HandlersProvider handlersProvider;


    @Override
    public Void visitMethod(MethodTree methodTree, ClassContext classContext) {
        JCTreeUtils.findVoidHandlersAndCall(methodTree,classContext);
        return super.visitMethod(methodTree, classContext);
    }

    @Override
    public Void visitReturn(ReturnTree var1, ClassContext var2) {

        return super.visitReturn(var1, var2);
    }

}
