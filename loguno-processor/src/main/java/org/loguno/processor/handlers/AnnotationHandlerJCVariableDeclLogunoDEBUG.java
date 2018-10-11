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

import java.util.stream.Collectors;
import java.util.stream.Stream;

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


        JCTree.JCLiteral value = factory.Literal(annotation.value()[0]);

        JCTree.JCLiteral paramName = factory.Literal(element.name.toString());

        JCTree.JCIdent param = factory.Ident(elements.getName(element.name.toString()));


        //method name can be taken from context
        //JCTree.JCLiteral methodname = factory.Literal(tree.getName().toString());

        //class name can be taken from context
        //JCTree.JCLiteral classname = factory.Literal(((Symbol.MethodSymbol) element).owner.getSimpleName().toString());

        String loggerName = classContext.getLoggerName();

        JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerName)), elements.getName("debug")),
                com.sun.tools.javac.util.List.<JCTree.JCExpression>of(value,paramName,param));

        JCTree.JCStatement callInfoMethodCall = factory.Exec(callInfoMethod);


        JCTree.JCBlock body = (JCTree.JCBlock) classContext.getCurrentBlock();


        //body.stats.append(callInfoMethodCall);

        //final List<JCTree.JCStatement> li = List.nil();

        ListBuffer<JCTree.JCStatement> li = new ListBuffer<>();



        body.stats.forEach(st -> {
            li.append(st);
            if(st==element)
                li.append(callInfoMethodCall);
        });

        body.stats = li.toList();

        //JCTree.JCBlock body = (JCTree.JCBlock) tree.getBody();
        //body.stats = body.stats.prepend(callInfoMethodCall);

    }
}
