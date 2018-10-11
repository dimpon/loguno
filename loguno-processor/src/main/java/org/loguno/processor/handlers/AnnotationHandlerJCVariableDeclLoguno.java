package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCTreeUtils;


/**
 * @author Dmitrii Ponomarev
 */
@Handler
@Order
public class AnnotationHandlerJCVariableDeclLoguno extends AnnotationHandlerBase<Loguno.DEBUG, JCTree.JCVariableDecl> {

    public AnnotationHandlerJCVariableDeclLoguno(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.DEBUG annotation, JCTree.JCVariableDecl element, ClassContext classContext) {


        String message = JCTreeUtils.getMessageTemplate(annotation.value(),ConfigurationKeys.LOCVAR_MESSAGE_PARAMS_PATTERN_DEFAULT);

        JCTree.JCLiteral value = factory.Literal(message);

        JCTree.JCLiteral paramName = factory.Literal(element.name.toString());

        JCTree.JCIdent param = factory.Ident(elements.getName(element.name.toString()));

        String loggerName = classContext.getLoggerName();

        JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerName)), elements.getName("debug")),
                com.sun.tools.javac.util.List.<JCTree.JCExpression>of(value,paramName,param));

        JCTree.JCStatement callInfoMethodCall = factory.Exec(callInfoMethod);


        JCTree.JCBlock body = (JCTree.JCBlock) classContext.getBlocks().getLast();

        ListBuffer<JCTree.JCStatement> li = new ListBuffer<>();

        body.stats.forEach(st -> {
            li.append(st);
            if(st==element)
                li.append(callInfoMethodCall);
        });

        body.stats = li.toList();

    }
}
