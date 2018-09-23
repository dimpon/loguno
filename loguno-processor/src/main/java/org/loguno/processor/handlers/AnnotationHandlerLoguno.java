package org.loguno.processor.handlers;

import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;
import com.sun.source.tree.MethodTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.ExecutableElement;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Handler
public class AnnotationHandlerLoguno extends AnnotationHandlerBase<Loguno, ExecutableElement> {
    @Override
    public void processTree(Loguno annotation, ExecutableElement element, JavacProcessingEnvironment env) {

        Trees trees = Trees.instance(env);
        Context context = env.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names names = Names.instance(context);

        Symtab symtab = Symtab.instance(context);
        Types types = Types.instance(context);
        JavacElements elements = JavacElements.instance(context);

        MethodTree method = trees.getTree(element);

        JCTree.JCExpression[] idents = method.getParameters().stream()
                .map(param -> Stream.<JCTree.JCExpression>of(
                        factory.Literal(param.getName().toString()),
                        factory.Ident(elements.getName(param.getName()))))
                .flatMap(s -> s)
                .toArray(JCTree.JCExpression[]::new);

        String message = ConfiguratorManager.getInstance().getConfiguration().getProperty(ConfigurationKeys.METHOD_MESSAGE_PATTERN_DEFAULT);
        String params = ConfiguratorManager.getInstance().getConfiguration().getProperty(ConfigurationKeys.METHOD_MESSAGE_PARAMS_PATTERN_DEFAULT);


        String paramsStr = method.getParameters().stream().map(o -> params).collect(Collectors.joining(","));
        JCTree.JCLiteral value = factory.Literal(message + paramsStr);


        JCTree.JCLiteral methodname = factory.Literal(method.getName().toString());

        JCTree.JCLiteral classname = factory.Literal(((Symbol.MethodSymbol) element).owner.getSimpleName().toString());


        JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName("LOG")), elements.getName("info")),
                com.sun.tools.javac.util.List.<JCTree.JCExpression>of(value, classname, methodname, idents));

        JCTree.JCStatement callInfoMethodCall = factory.Exec(callInfoMethod);


        JCTree.JCBlock body = (JCTree.JCBlock) method.getBody();
        body.stats = body.stats.prepend(callInfoMethodCall);
    }


}
