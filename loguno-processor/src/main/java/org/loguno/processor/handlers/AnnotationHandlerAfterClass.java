package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.Loguno;

/**
 * @author Dmitrii Ponomarev
 */
@Handler(value = Handler.RunOrder.AFTER,order = 10)
public class AnnotationHandlerAfterClass extends AnnotationHandlerBase<Loguno.Logger, JCTree.JCClassDecl>  {
    public AnnotationHandlerAfterClass(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Logger annotation, JCTree.JCClassDecl element, ClassContext classContext) {
        classContext.getLoggers().removeLast();
        classContext.getClasses().removeLast();
        classContext.getWhereIam().removeLast();
    }
}
