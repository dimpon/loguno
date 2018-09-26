package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.source.tree.ClassTree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

@Order(0)
public class AnnotationHandlerSlf4j extends AnnotationHandlerBase<Loguno.Slf4j, TypeElement> {

    @Override
    public void processTree(Loguno.Slf4j annotation, TypeElement typeElement, JavacProcessingEnvironment env) {

        Trees trees = Trees.instance(env);
        Context context = env.getContext();
        TreeMaker factory = TreeMaker.instance(context);
        Names names = Names.instance(context);

        Symtab symtab = Symtab.instance(context);
        Types types = Types.instance(context);
        JavacElements elements = JavacElements.instance(context);

        ClassTree tree = trees.getTree(typeElement);


        JCTree.JCModifiers modifiers = factory.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);

        Name variableName = names.fromString("LOG");

        Name name = ((JCTree.JCClassDecl) tree).getSimpleName();
        JCTree.JCFieldAccess aClass = factory.Select(factory.Ident(name), elements.getName("class"));

        JCTree.JCExpression type = factory.Select(factory.Select(factory.Ident(elements.getName("org")),
                elements.getName("slf4j")),
                elements.getName("Logger"));

        JCTree.JCExpression method = factory.Select(
                factory.Select(
                        factory.Select(
                                factory.Ident(elements.getName("org")),
                                elements.getName("slf4j")),
                        elements.getName("LoggerFactory")),
                elements.getName("getLogger"));

        JCTree.JCMethodInvocation factoryMethodCall =
                factory.Apply(com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                        method, com.sun.tools.javac.util.List.<JCTree.JCExpression>of(aClass));

        JCTree.JCVariableDecl logVar = factory.at(((JCTree) tree).pos).VarDef(modifiers, variableName, type, factoryMethodCall);

        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) tree;
        classDecl.defs = classDecl.defs.append(logVar);

        System.out.println("AnnotationHandlerSlf4j: " + annotation.value());
    }
}
