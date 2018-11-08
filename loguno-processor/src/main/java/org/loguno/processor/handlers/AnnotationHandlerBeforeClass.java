package org.loguno.processor.handlers;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Ponomarev
 */
@Handler(value = Handler.RunOrder.BEFORE,order = 1)
public class AnnotationHandlerBeforeClass extends AnnotationHandlerBase<Loguno.Logger, JCTree.JCClassDecl>  {
    public AnnotationHandlerBeforeClass(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Override
    public void processTree(Loguno.Logger annotation, JCTree.JCClassDecl classDecl, ClassContext classContext) {

        Symbol.ClassSymbol sym = classDecl.sym;

        classContext.getClasses().addLast(sym.fullname.toString());


        Frameworks loggerFramework = annotation.value();

        if (loggerFramework == Frameworks.DEFAULT) {
            loggerFramework = ConfiguratorManager.getInstance().getConfiguration().getProperty(ConfigurationKeys.LOGGING_FRAMEWORK_DEFAULT);
        }

        ClassContext.LoggerInfo currentLogger = ClassContext.LoggerInfo.of(loggerFramework, annotation.name(), annotation.lazy());
        classContext.getLoggers().addLast(currentLogger);

    }

}
