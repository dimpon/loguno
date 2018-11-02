package org.loguno.processor;

import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import lombok.AllArgsConstructor;
import org.loguno.Loguno;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.handlers.InstrumentsHolder;
import org.loguno.processor.utils.AnnotationUtils;
import sun.reflect.annotation.AnnotationParser;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author Dmitrii Ponomarev
 */
public class LogunoTreeScanner extends TreeScanner<Void, ClassContext> {


    private HandlersProvider handlersProvider;
    private InstrumentsHolder holder;

    LogunoTreeScanner(InstrumentsHolder holder) {
        super();
        this.handlersProvider = HandlersProvider.instance();
        this.holder = holder;
    }


    @Override
    public Void visitClass(ClassTree classTree, ClassContext context) {

        java.util.List<? extends AnnotationTree> annotations =  classTree.getModifiers().getAnnotations();
        System.out.println(classTree.getSimpleName());
        annotations.forEach(o -> {
            Type type = ((JCTree.JCAnnotation) o).annotationType.type;
            System.out.println(type+" "+((JCTree.JCAnnotation) o).annotationType);

        });

        handlersProvider.supportedAnnotations().forEach(aClass -> {
            Annotation[] annotationsByType = ((JCTree.JCClassDecl) classTree).sym.getAnnotationsByType(aClass);
            int length = annotationsByType.length;
        });


        return super.visitClass(classTree, context);
    }

    @Override
    public Void visitMethod(MethodTree methodTree, ClassContext context) {

        //List<JCTree.JCAnnotation> annotations = ((JCTree.JCClassDecl) methodTree).mods.annotations;

        java.util.List<? extends AnnotationTree> annotations =  methodTree.getModifiers().getAnnotations();

        System.out.println(methodTree.getName());
        annotations.forEach(o -> {
            Type type = ((JCTree.JCAnnotation) o).annotationType.type;
            System.out.println(type+" "+((JCTree.JCAnnotation) o).annotationType);

        });


        if (((JCTree.JCMethodDecl) methodTree).sym != null) {
            handlersProvider.supportedAnnotations().forEach(aClass -> {
                Annotation[] annotationsByType = ((JCTree.JCMethodDecl) methodTree).sym.getAnnotationsByType(aClass);
                int length = annotationsByType.length;
            });
        }

        return super.visitMethod(methodTree, context);
    }

    @Override
    public Void visitVariable(VariableTree variableTree, ClassContext context) {


        java.util.List<? extends AnnotationTree> annotations =  variableTree.getModifiers().getAnnotations();

        System.out.println(variableTree.getName());
        annotations.forEach(o -> {
            Type type = ((JCTree.JCAnnotation) o).annotationType.type;
            System.out.println(type+" "+((JCTree.JCAnnotation) o).annotationType);

        });


        //((JCTree.JCVariableDecl) variableTree).sym.owner;
        if (((JCTree.JCVariableDecl) variableTree).sym != null) {
            handlersProvider.supportedAnnotations().forEach(aClass -> {
                Annotation[] annotationsByType = ((JCTree.JCVariableDecl) variableTree).sym.getAnnotationsByType(aClass);
                int length = annotationsByType.length;
            });
        }

        return super.visitVariable(variableTree, context);
    }

    @Override
    public Void visitCatch(CatchTree catchTree, ClassContext context) {
        return super.visitCatch(catchTree, context);
    }


    @Override
    public Void visitBlock(BlockTree blockTree, ClassContext context) {
        return super.visitBlock(blockTree, context);
    }


    private Void scanAndReduce(Tree var1, ClassContext var2, Void var3) {
        return this.reduce(this.scan(var1, var2), var3);
    }

    private Void scanAndReduce(Iterable<? extends Tree> var1, ClassContext var2, Void var3) {
        return this.reduce(this.scan(var1, var2), var3);
    }
}
