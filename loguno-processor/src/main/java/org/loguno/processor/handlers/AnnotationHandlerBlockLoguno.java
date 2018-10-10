package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.Loguno;

import javax.lang.model.element.ExecutableElement;

/**
 * @author Dmitrii Ponomarev
 */
@Handler
@Order
public class AnnotationHandlerBlockLoguno extends AnnotationHandlerBase<Loguno, JCTree.JCBlock> {

    public AnnotationHandlerBlockLoguno(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno annotation, JCTree.JCBlock element, ClassContext classContext) {

        List<JCTree.JCStatement> stats = element.stats;

    }
}
