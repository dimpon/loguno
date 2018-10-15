package org.loguno.processor.handlers;

import com.sun.source.tree.Tree;
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
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLoggerBuilder;

import javax.lang.model.element.*;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.NestingKind.MEMBER;

@Handler
@Order(1)
public class AnnotationHandlerLogger extends AnnotationHandlerBase<Loguno.Logger, TypeElement> {

    private static final String loggerClass = "org.slf4j.Logger";

    private static final String factoryClassAndMethod = "org.slf4j.LoggerFactory.getLogger";

    private static final String lazyFactoryClassAndMethod = "org.loguno.lazy.LazyLoggerFactorySlf4j.getLogger";

    public AnnotationHandlerLogger(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Logger annotation, TypeElement typeElement, ClassContext classContext) {

        SupportedLoggers loggerFramework = annotation.value();

        if (loggerFramework == SupportedLoggers.Empty) {
            loggerFramework = conf.getProperty(ConfigurationKeys.LOGGING_FRAMEWORK_DEFAULT);
        }

        ClassContext.LoggerInfo currentLogger = ClassContext.LoggerInfo.of(loggerFramework, annotation.name(), annotation.lazy());
        classContext.getLoggers().addLast(currentLogger);

        ////////////////////////////////

        ClassTree classTree = trees.getTree(typeElement);

        boolean isStatic = true;

        //inner non-static classes cannnot have static fields
        if (typeElement.getKind() == CLASS
                && typeElement.getNestingKind() == MEMBER
                && !typeElement.getModifiers().contains(Modifier.STATIC)) {
            isStatic = false;
        }

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

        classDecl.defs = classDecl.defs.append(logVar1);

    }
}
