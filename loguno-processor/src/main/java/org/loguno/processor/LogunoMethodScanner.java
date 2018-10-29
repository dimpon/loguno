package org.loguno.processor;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.TreeScanner;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.utils.JCTreeUtils;

import java.util.List;

public class LogunoMethodScanner extends TreeScanner<Void, ClassContext> {


    @Override
    public Void visitMethod(MethodTree methodTree, ClassContext classContext) {
        if (methodTree.getBody() != null) {

            List<? extends AnnotationTree> variableAnnotations = methodTree.getModifiers().getAnnotations();

            System.out.printf("");

            //JCTreeUtils.findVoidHandlersAndCall(methodTree, classContext);
        }
        return super.visitMethod(methodTree, classContext);
    }
}
