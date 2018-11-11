package org.loguno.processor.handlers;

import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.Types;
import com.sun.tools.javac.comp.Enter;
import com.sun.tools.javac.comp.MemberEnter;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.utils.annotations.AnnotationRetriever;
import org.loguno.processor.utils.annotations.AnnotationRetrieverImpl;

import javax.annotation.processing.Filer;

public class InstrumentsHolder {

    public final JavacProcessingEnvironment environment;

    public final Trees trees;
    public final Context context;
    public final TreeMaker factory;
    public final Names names;

    public final Symtab symtab;
    public final Types types;
    public final JavacElements elements;
    public final Filer filer;
    public Enter enter;
    public MemberEnter memberEnter;
    public Configuration conf = ConfiguratorManager.getInstance().getConfiguration();
    public final AnnotationRetriever retriever = new AnnotationRetrieverImpl();


    public InstrumentsHolder(JavacProcessingEnvironment environment) {
        this.environment = environment;

        this.trees = Trees.instance(environment);
        this.context = environment.getContext();
        this.factory = TreeMaker.instance(context);
        this.names = Names.instance(context);

        this.symtab = Symtab.instance(context);
        this.types = Types.instance(context);
        this.elements = JavacElements.instance(context);
        this.filer = environment.getFiler();
        this.enter = Enter.instance(context);
        this.memberEnter = MemberEnter.instance(context);
    }


}
