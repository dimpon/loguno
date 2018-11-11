package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;

import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import org.loguno.processor.LogunoScanner;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.stream.Collectors;

public abstract class AnnotationHandlerMethodThrows<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

    protected AnnotationHandlerMethodThrows(JavacProcessingEnvironment environment) {
        super(environment);
    }

    /**
     * this method is invoke for every method.
     */
    @Handler
    public static class AnnotationHandlerWholeMethod extends AnnotationHandlerBase<VoidAnnotation, LogunoScanner.ThrowsOfMethod> {

        public AnnotationHandlerWholeMethod(JavacProcessingEnvironment environment) {
            super(environment);
        }

        @Override
        public void processTree(VoidAnnotation annotation, LogunoScanner.ThrowsOfMethod element, ClassContext classContext) {
            //has method throws?

            List<JCTree.JCExpression> aThrows = element.thrown();

            if (aThrows == null || aThrows.isEmpty())
                return;

            JCTree.JCMethodDecl method = (JCTree.JCMethodDecl) classContext.getBreadcrumb().getLast();

            if (method.getBody().stats == null || method.getBody().stats.isEmpty())
                return;


            int i = 1;

            String e = "e";

            List<AnnotationHandlerPipedExceptionsCatch.JCStatementHolder> holders = new ArrayList<>();


            for (JCTree.JCExpression oneException : aThrows) {
                final String excepVarName = e + i;
                i++;
                if (oneException instanceof JCTree.JCAnnotatedType) {
                    JCTree.JCAnnotatedType oneAnnotatedException = (JCTree.JCAnnotatedType) oneException;

                    java.util.List<Annotation> annotations = retriever
                            .getTreeAnnotations(oneAnnotatedException.getAnnotations()).collect(Collectors.toList());

                    final AnnotationHandlerPipedExceptionsCatch.JCStatementHolder holder = AnnotationHandlerPipedExceptionsCatch.JCStatementHolder.of()
                            .exceptionType(oneAnnotatedException)
                            .element(method)
                            .exceptionName(excepVarName);

                    annotations.forEach(ann ->
                            HandlersProvider.instance().getHandlersBeforeByElementAndAnnotation(ann.annotationType(), holder)
                                    .forEach(h -> h.process(ann, holder, classContext)));

                    if (holder.getLogMethods().size() != 0) {
                        holders.add(holder);
                    }
                }
            }

            //here we have all log messages created and sorted
            //and can build try-catch block
            if (holders.isEmpty())
                return;


            ListBuffer<JCTree.JCCatch> catches = new ListBuffer<>();

            holders.forEach(h -> {

                Name ee = names.fromString(h.exceptionName());

                JCTree.JCExpression excepType = h.exceptionType().getUnderlyingType();

                JCTree.JCThrow throwExpression = factory.Throw(factory.Ident(elements.getName(h.exceptionName())));

                JCTree.JCVariableDecl var = factory.at(method.pos).VarDef(factory.Modifiers(0), ee, excepType, null);
                JCTree.JCCatch aCatch = factory.Catch(var,
                        factory.Block(0, h.getLogMethods().toList().append(throwExpression))
                );

                catches.append(aCatch);

            });

            method.getBody().stats = com.sun.tools.javac.util.List.<JCTree.JCStatement>of(
                    factory.at(method.pos()).Try(
                            factory.Block(0, method.getBody().stats),
                            catches.toList(),
                            null//factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>nil())

                    ));

            /*


            for (JCTree.JCExpression oneException : aThrows) {

                final String excepVarName = e + i;
                i++;

                if (oneException instanceof JCTree.JCAnnotatedType) {
                    JCTree.JCAnnotatedType oneAnnotatedException = (JCTree.JCAnnotatedType) oneException;

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
                            factory.Block(0, holder.$logMethods.toList())
                    );

                    catches.append(aCatch);
                }
            }

            element.getBody().stats = com.sun.tools.javac.util.List.<JCTree.JCStatement>of(
                    factory.at(element.pos()).Try(
                            factory.Block(0, element.getBody().stats),
                            catches.toList(),
                            null//factory.Block(0,com.sun.tools.javac.util.List.<JCTree.JCStatement>nil())

                    ));
*/
        }
    }
}
