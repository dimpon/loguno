package org.loguno.processor.handlers;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfiguratorManager;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

abstract public class AnnotationHandlerBase<A extends Annotation, E extends Element> implements AnnotationHandler<A, E> {

    protected final JavacProcessingEnvironment environment;

    protected final Trees trees;
    protected final Context context;
    protected final TreeMaker factory;
    protected final Names names;

    protected final Symtab symtab;
    protected final Types types;
    protected final JavacElements elements;
    protected final Filer filer;
    protected Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

    protected AnnotationHandlerBase(JavacProcessingEnvironment environment) {
        this.environment = environment;

        this.trees = Trees.instance(environment);
        this.context = environment.getContext();
        this.factory = TreeMaker.instance(context);
        this.names = Names.instance(context);

        this.symtab = Symtab.instance(context);
        this.types = Types.instance(context);
        this.elements = JavacElements.instance(context);
        this.filer = environment.getFiler();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<A> getAnnotationClass() {
        return (Class<A>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<E> getElementClass() {
        return (Class<E>) ((ParameterizedType) this.getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];
    }


    protected JCTree.JCExpression createJCExpression(final String fullName) {
        String[] splitted = fullName.split("\\.");
        return doRound(splitted, (splitted.length - 1));
    }

    private JCTree.JCExpression doRound(String[] splitted, int i) {
        if (i == 0)
            return factory.Ident(elements.getName(splitted[i]));
        return factory.Select(doRound(splitted, i - 1), elements.getName(splitted[i]));
    }

}
