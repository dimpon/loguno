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

@Handler
@Order(1)
public class AnnotationHandlerSlf4j extends AnnotationHandlerBase<Loguno.Slf4j, TypeElement> {

    private static final String loggerClass = "org.slf4j.Logger";

    private static final String factoryClassAndMethod = "org.slf4j.LoggerFactory.getLogger";

    private static final String lazyFactoryClassAndMethod = "org.loguno.lazy.LazyLoggerFactorySlf4j.getLogger";

    public AnnotationHandlerSlf4j(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Slf4j annotation, TypeElement typeElement, ClassContext classContext) {

        ClassTree tree = trees.getTree(typeElement);

        JCTree.JCModifiers modifiers = factory.Modifiers(Flags.PRIVATE | Flags.STATIC | Flags.FINAL);

        Name loggerName = names.fromString(annotation.value());//takes logger name from annotation


        Name name = ((JCTree.JCClassDecl) tree).getSimpleName();

        JCTree.JCFieldAccess elementTypeForPassingToLogger = factory.Select(factory.Ident(name), elements.getName("class"));

        JCTree.JCExpression loggerType = createJCExpression(loggerClass);

        String factoryClassAndMethodToCreate = (annotation.lazy())?lazyFactoryClassAndMethod:factoryClassAndMethod;

        JCTree.JCExpression method = createJCExpression(factoryClassAndMethodToCreate);

        JCTree.JCMethodInvocation factoryMethodCall =
                factory.Apply(com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                        method, com.sun.tools.javac.util.List.<JCTree.JCExpression>of(elementTypeForPassingToLogger));

        JCTree.JCVariableDecl logVar = factory.at(((JCTree) tree).pos).VarDef(modifiers, loggerName, loggerType, factoryMethodCall);

        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) tree;
        classDecl.defs = classDecl.defs.append(logVar);

        classContext.setLoggerName(annotation.value());
        classContext.setLazy(annotation.lazy());
        classContext.setLogger(ClassContext.Logger.Slf4j);

    }
}
