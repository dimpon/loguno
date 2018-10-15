package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Ponomarev
 */

public abstract class AnnotationHandlerLocalVariable<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerLocalVariable(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerLocalVariable<Loguno, JCTree.JCVariableDecl> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCTree.JCVariableDecl element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, JCTree.JCVariableDecl element, ClassContext classContext) {

        String message = JCTreeUtils.tryToInsertClassAndMethodName(
                JCTreeUtils.getMessageTemplate(value, ConfigurationKeys.LOCVAR_MESSAGE_PATTERN_DEFAULT), classContext);

        JCTree.JCLiteral wholeMessage = factory.Literal(message);

        JCTree.JCLiteral paramName = factory.Literal(element.name.toString());

        JCTree.JCIdent param = factory.Ident(elements.getName(element.name.toString()));

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerVariable)), elements.getName(logMethod)),
                com.sun.tools.javac.util.List.<JCTree.JCExpression>of(wholeMessage, paramName, param));

        JCTree.JCStatement callInfoMethodCall = factory.at(element.pos).Exec(callInfoMethod);

        JCTree.JCBlock body = (JCTree.JCBlock) classContext.getBlocks().getLast();

        ListBuffer<JCTree.JCStatement> li = new ListBuffer<>();

        body.stats.forEach(st -> {
            li.append(st);
            if (st == element)
                li.append(callInfoMethodCall);
        });

        body.stats = li.toList();
    }
}
