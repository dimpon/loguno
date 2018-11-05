package org.loguno.processor.handlers;

import org.loguno.Loguno;
import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLoggerBuilder;

import javax.lang.model.element.*;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.NestingKind.MEMBER;

@Handler
@Order(value = 2,runOrder = Order.RunOrder.BEFORE)
public class AnnotationHandlerLogger extends AnnotationHandlerBase<Loguno.Logger, JCTree.JCClassDecl> {

    private static final String loggerClass = "org.slf4j.Logger";

    private static final String factoryClassAndMethod = "org.slf4j.LoggerFactory.getLogger";

    private static final String lazyFactoryClassAndMethod = "org.loguno.lazy.LazyLoggerFactorySlf4j.getLogger";

    public AnnotationHandlerLogger(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Logger annotation, JCTree.JCClassDecl classTree, ClassContext classContext) {

        ClassContext.LoggerInfo currentLogger = classContext.getLoggers().getLast();

        ////////////////////////////////

        //ClassTree classTree = trees.getTree(typeElement);

        boolean isStatic = true;

        //inner non-static classes cannnot have static fields
        /*if (typeElement.getKind() == CLASS
                && typeElement.getNestingKind() == MEMBER
                && !typeElement.getModifiers().contains(Modifier.STATIC)) {
            isStatic = false;
        }*/

        String factoryClassAndMethod = (currentLogger.isLazy()) ?
                AnnotationHandlerLogger.lazyFactoryClassAndMethod : AnnotationHandlerLogger.factoryClassAndMethod;


        JCTree.JCVariableDecl logVar1 = JCLoggerBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .classTree(classTree)
                .loggerName(currentLogger.getLoggerName())
                .factoryClassAndMethod(factoryClassAndMethod)
                .loggerClass(loggerClass)
                .topLevelClass(classTree.getSimpleName().toString())
                .modifier(Flags.PRIVATE)
                .modifier(((isStatic) ? Flags.STATIC : 0))
                .modifier(Flags.FINAL)
                .build()
                .create();

        JCTree.JCClassDecl classDecl = (JCTree.JCClassDecl) classTree;

        classDecl.defs = classDecl.defs.prepend(logVar1);

    }
}
