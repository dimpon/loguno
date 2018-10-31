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

import javax.annotation.processing.Filer;

public class InstrumentsHolder {

    protected final JavacProcessingEnvironment environment;

    protected final Trees trees;
    protected final Context context;
    protected final TreeMaker factory;
    protected final Names names;

    protected final Symtab symtab;
    protected final Types types;
    protected final JavacElements elements;
    protected final Filer filer;
    protected Enter enter;
    protected MemberEnter memberEnter;
    protected Configuration conf = ConfiguratorManager.getInstance().getConfiguration();


    protected InstrumentsHolder(JavacProcessingEnvironment environment) {
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
