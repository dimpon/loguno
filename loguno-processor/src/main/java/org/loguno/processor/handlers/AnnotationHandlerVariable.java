package org.loguno.processor.handlers;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKey;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import static org.loguno.processor.configuration.ConfigurationKeys.METHODPARAM_MESSAGE_PATTERN_DEFAULT;


/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerVariable <A extends Annotation, E> extends AnnotationHandlerBase<A, E> {


    protected AnnotationHandlerVariable(JavacProcessingEnvironment environment) {
        super(environment);
    }

    @Handler
    @Order
    public static class AnnotationHandlerLoguno extends AnnotationHandlerVariable<Loguno, VariableElement> {

        public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(Loguno annotation, VariableElement element, ClassContext classContext) {
            annotation.value();

            doRealJob(annotation.value(),"info",element,classContext);

            //String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
            //doRealJob(annotation.value(), method, element, classContext);
        }
    }


    public void doRealJob(String[] value, String logMethod, VariableElement element, ClassContext classContext) {

        /*MethodTree tree = trees.getTree(element);

        //generate array of pairs - param name-param value
        JCTree.JCExpression[] idents = tree.getParameters().stream()
                .map(param -> Stream.<JCTree.JCExpression>of(
                        factory.Literal(param.getName().toString()),
                        factory.Ident(elements.getName(param.getName()))))
                .flatMap(s -> s)
                .toArray(JCTree.JCExpression[]::new);
*/

        String message = JCTreeUtils.tryToInsertClassAndMethodName(JCTreeUtils.getMessageTemplate(value, METHODPARAM_MESSAGE_PATTERN_DEFAULT), classContext);

        VariableTree tree = (VariableTree)elements.getTree(element);

        Tree tree1 = trees.getTree(element.getEnclosingElement());

        Tree.Kind kind = tree1.getKind();




        Symbol.VarSymbol el = (Symbol.VarSymbol)element;


        //factory.at(((Symbol.VarSymbol) element).pos)


        //MethodTree tree = (MethodTree)trees.getTree(el.owner);

        //Tree tree = trees.getTree(element);






    }
}
