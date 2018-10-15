package org.loguno.processor.handlers;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;


/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerMethodParams<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerMethodParams(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerMethodParams<Loguno, VariableElement> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, VariableElement element, ClassContext classContext) {
            doRealJob(annotation.value(),"info",element,classContext);

            //String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            //doRealJob(annotation.value(), method, element, classContext);
        }
    }


     void doRealJob(String[] value, String logMethod, VariableElement element, ClassContext classContext) {



        /*MethodTree tree = trees.getTree(element);

        //generate array of pairs - param name-param value
        JCTree.JCExpression[] idents = tree.getParameters().stream()
                .map(param -> Stream.<JCTree.JCExpression>of(
                        factory.Literal(param.getName().toString()),
                        factory.Ident(elements.getName(param.getName()))))
                .flatMap(s -> s)
                .toArray(JCTree.JCExpression[]::new);
*/

        String message = JCTreeUtils.tryToInsertClassAndMethodName(JCTreeUtils.getMessageTemplate(value, ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT), classContext);

        VariableTree variableTree = (VariableTree)elements.getTree(element);

        JCTree.JCLiteral wholeMessage = factory.Literal(message);
        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        final ListBuffer<JCTree.JCExpression> buffer = new ListBuffer<>();
        buffer.append(wholeMessage);

        if (message.contains("{}")) {
            buffer.append(factory.Literal(variableTree.getName().toString()));
            buffer.append(factory.Ident(elements.getName(variableTree.getName())));
        }

        JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression> nil(),
                factory.Select(factory.Ident(elements.getName(loggerVariable)), elements.getName(logMethod)),
                buffer.toList());

        JCTree.JCStatement callInfoMethodCall = factory.at(((JCTree) variableTree).pos).Exec(callInfoMethod);

        ExecutableElement enclosingElement = (ExecutableElement)element.getEnclosingElement();

        MethodTree methodTree = (MethodTree)trees.getTree(element.getEnclosingElement());

        JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();


        if (enclosingElement.getKind() == ElementKind.CONSTRUCTOR &&
                body.stats.size() > 0 &&
                body.stats.get(0) != null &&
                body.stats.get(0).toString().contains("super")) {

            ListBuffer<JCTree.JCStatement> bodyNew = new ListBuffer<>();
            bodyNew.append(body.stats.get(0));
            bodyNew.append(callInfoMethodCall);

            for (int i = 1; i < body.stats.size(); i++) {
                bodyNew.append(body.stats.get(i));
            }

            body.stats = bodyNew.toList();

        } else {
            body.stats = body.stats.prepend(callInfoMethodCall);
        }

    }
}
