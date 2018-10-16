package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;

public abstract class AnnotationHandlerCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerCatch(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerCatch<Loguno, JCTree.JCCatch> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, JCTree.JCCatch element, ClassContext classContext) {
            String method = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
            doRealJob(annotation.value(), method, element, classContext);
        }
    }

    void doRealJob(String[] value, String logMethod, JCTree.JCCatch element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element(element)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .build()
                .addParam(element.getParameter().getName().toString())
                .create();

        //JCTree.JCLiteral wholeMessage = factory.Literal(message);

        //JCTree.JCLiteral paramName = factory.Literal(element.name.toString());

        //JCTree.JCLiteral paramName = factory.Literal(element.param.toString());

        //JCTree.JCIdent param = factory.Ident(element.param.name);



       /* JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerVariable)), elements.getName(logMethod)),
                com.sun.tools.javac.util.List.<JCTree.JCExpression>of(wholeMessage,param));

        JCTree.JCStatement callInfoMethodCall = factory.at(element.pos).Exec(callInfoMethod);
*/
        JCTree.JCBlock body = element.getBlock();

        //ListBuffer<JCTree.JCStatement> li = new ListBuffer<>();

        body.stats = body.stats.prepend(methodCall);


    }
}
