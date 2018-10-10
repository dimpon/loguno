package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.Loguno;

/**
 * @author Dmitrii Ponomarev
 */
@Handler
@Order
public class AnnotationHandlerJCVariableDeclLogunoDEBUG extends AnnotationHandlerBase<Loguno.DEBUG, JCTree.JCVariableDecl> {

    public AnnotationHandlerJCVariableDeclLogunoDEBUG(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.DEBUG annotation, JCTree.JCVariableDecl element, ClassContext classContext) {

        String value = annotation.string();
        /*String string = annotation.string();
        boolean context = annotation.context();
        ClassContext.Logger logger = annotation.logger();*/

        JCTree.JCModifiers modifiers = element.getModifiers();

    }
}
