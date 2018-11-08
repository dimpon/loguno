package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCExpressionBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AnnotationHandlerMethodThrows <A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerMethodThrows(JavacProcessingEnvironment environment) {
        super(environment);
    }

    /**
     * this method is invoke for every method.
     */
    //@Handler
    @Order
    public static class AnnotationHandlerWholeMethod extends AnnotationHandlerMethod<VoidAnnotation, JCTree.JCMethodDecl> {

        public AnnotationHandlerWholeMethod(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, JCTree.JCMethodDecl element, ClassContext classContext) {
            //has method throws?
            if (element.getThrows().isEmpty())
                return;
            //has method body?
            if(element.getBody().stats==null || element.getBody().stats.isEmpty())
                return;

            List<JCTree.JCExpression> aThrows = element.getThrows();

            int i=1;

            String e = "e";
            ListBuffer<JCTree.JCCatch> catches = new ListBuffer<>();

            //java.util.List<AnnotationHandlerPipedExceptionsCatch.JCStatementHolder> holders = new ArrayList<>();

            for (JCTree.JCExpression oneException:aThrows) {

                final String excepVarName = e+i;
                i++;

                if(oneException instanceof JCTree.JCAnnotatedType) {
                    JCTree.JCAnnotatedType oneAnnotatedException =(JCTree.JCAnnotatedType)oneException;

                    final AnnotationHandlerPipedExceptionsCatch.JCStatementHolder holder = AnnotationHandlerPipedExceptionsCatch.JCStatementHolder.of()
                            .element(element)
                            .exceptionName(excepVarName);

                    oneAnnotatedException.getAnnotations().forEach(ann -> {
                        JCTreeUtils.findHandlersAndCall(ann, holder, classContext);
                    });

                    Name ee = names.fromString(holder.exceptionName());

                    JCTree.JCExpression excepType = oneAnnotatedException.getUnderlyingType();
                    JCTree.JCThrow aThrow = factory.Throw(factory.Ident(elements.getName(holder.exceptionName())));
                    holder.add(aThrow);

                    JCTree.JCVariableDecl var = factory.at(((JCTree) element).pos).VarDef(factory.Modifiers(0), ee, excepType, null);
                    JCTree.JCCatch aCatch = factory.Catch(var,
                            factory.Block(0,holder.$logMethods.toList() )
                    );

                    catches.append(aCatch);
                }
            }

            element.getBody().stats=com.sun.tools.javac.util.List.<JCTree.JCStatement>of(
                    factory.at(element.pos()).Try(
                            factory.Block(0, element.getBody().stats),
                            catches.toList(),
                            null//factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>nil())

                    ));


            //String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);



            /*JCTree.JCModifiers jcModifiers = factory.Modifiers(0);
            Name ee = names.fromString("e");
            JCTree.JCExpression excepType = expressionBuilder.createJCExpression("Exception");

            JCTree.JCVariableDecl var = factory.at(((JCTree) element).pos).VarDef(jcModifiers, ee, excepType, null);

            JCTree.JCStatement throww = factory.Throw(factory.Ident(elements.getName("e")));

            element.getBody().stats=com.sun.tools.javac.util.List.<JCTree.JCStatement>of(
                    factory.Try(
                            factory.Block(0, element.getBody().stats),

                            com.sun.tools.javac.util.List.<JCTree.JCCatch>of(factory.Catch(var,
                                    factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>of(throww))
                                    )),
                            null//factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>nil())

                    ));*/

        }
    }
}
