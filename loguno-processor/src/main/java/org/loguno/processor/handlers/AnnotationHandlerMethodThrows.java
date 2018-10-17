package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Name;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCExpressionBuilder;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;

public abstract class AnnotationHandlerMethodThrows <A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerMethodThrows(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerWholeMethod extends AnnotationHandlerMethod<VoidAnnotation, JCTree.JCMethodDecl> {

        public AnnotationHandlerWholeMethod(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            if (element.getThrows().isEmpty())
                return;

            //String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);

            JCExpressionBuilder expressionBuilder = JCExpressionBuilder.builder()
                    .elements(elements)
                    .factory(factory)
                    .build();

            JCTree.JCModifiers jcModifiers = factory.Modifiers(0);
            Name e = names.fromString("e");
            JCTree.JCExpression excepType = expressionBuilder.createJCExpression("Exception");

            JCTree.JCVariableDecl var = factory.at(((JCTree) element).pos).VarDef(jcModifiers, e, excepType, null);

            JCTree.JCStatement throww = factory.Throw(factory.Ident(elements.getName("e")));


            element.getBody().stats=com.sun.tools.javac.util.List.<JCTree.JCStatement>of(
                    factory.Try(
                            factory.Block(0, element.getBody().stats),

                            com.sun.tools.javac.util.List.<JCTree.JCCatch>of(factory.Catch(var,
                                    factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>of(throww))
                                    )),
                            null//factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>nil())

                    ));

        }
    }
}
