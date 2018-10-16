package org.loguno.processor.handlers;

import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
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
            doRealJob(annotation.value(), "info", element, classContext);

            //String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            //doRealJob(annotation.value(), method, element, classContext);
        }
    }


    void doRealJob(String[] value, String logMethod, VariableElement element, ClassContext classContext) {

        String message = JCTreeUtils.message(value, ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT, classContext);

        VariableTree elementTree = (VariableTree) elements.getTree(element);

        String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

        JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
                .factory(factory)
                .elements(elements)
                .names(names)
                .element((JCTree) elementTree)
                .loggerName(loggerVariable)
                .logMethod(logMethod)
                .message(message)
                .build()
                .addParamPair(elementTree.getName().toString())
                .create();

        ExecutableElement methodElement = (ExecutableElement) element.getEnclosingElement();

        MethodTree methodTree = (MethodTree) trees.getTree(element.getEnclosingElement());

        JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();

        body.stats = JCTreeUtils.generateNewMethodBody(methodElement, trees,methodCall);


        /*if (JCTreeUtils.isMethodConstructorWithSuper(methodElement, trees)) {

            ListBuffer<JCTree.JCStatement> bodyNew = new ListBuffer<>();
            bodyNew.append(body.stats.get(0));
            bodyNew.append(methodCall);

            for (int i = 1; i < body.stats.size(); i++) {
                bodyNew.append(body.stats.get(i));
            }

            body.stats = bodyNew.toList();
        } else {
            body.stats = body.stats.prepend(methodCall);
        }*/
    }
}
