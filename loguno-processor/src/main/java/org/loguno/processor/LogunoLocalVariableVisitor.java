package org.loguno.processor;

import com.sun.source.tree.AnnotatedTypeTree;
import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import lombok.AllArgsConstructor;
import org.loguno.processor.handlers.ClassContext;

/**
 * @author Dmitrii Ponomarev
 */
@AllArgsConstructor
public class LogunoLocalVariableVisitor extends TreeScanner<Void, ClassContext> {


    private final JCTree.JCMethodDecl method;
    private final JavacProcessingEnvironment environment;

    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext classContext) {


        Context context = environment.getContext();
        TreeMaker instance = TreeMaker.instance(context);

        JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl) variableTree;

        if(variable.sym == null){//it catches method parameters also. local vars has sym = null.

            List<JCTree.JCAnnotation> annotations = variable.getModifiers().getAnnotations();



            //instance.

            annotations.forEach(a -> {

                if(a.toString().equals("@Loguno()")){

                    ListBuffer newStatements = new ListBuffer();



                }




            });

            //annotations.stream().filter(a -> a.attribute.)



        }

        return super.visitVariable(variableTree, classContext);
    }



    @Override
    public Void visitAnnotation(AnnotationTree var1, ClassContext var2) {
        return super.visitAnnotation(var1, var2);
    }

    @Override
    public Void visitAnnotatedType(AnnotatedTypeTree var1, ClassContext var2) {
        return super.visitAnnotatedType(var1, var2);
    }
}
