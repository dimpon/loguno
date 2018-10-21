package org.loguno.processor.utils;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import com.sun.tools.javac.util.Pair;
import lombok.Builder;
import lombok.Singular;

import java.util.List;


@Builder
public class JCLogMethodBuilder {

    private final TreeMaker factory;

    private final Names names;

    private final JavacElements elements;

    private final String message;

    private final String loggerName;

    private final String logMethod;

    private final JCTree element;

    @Singular
    private final List<Pair<ParamType,String>> params;

    private final ListBuffer<JCTree.JCExpression> $buffer = new ListBuffer<>();

    public JCTree.JCStatement create() {


        params.forEach((p) -> {

            if(p.fst==ParamType.VAR){
                JCTree.JCIdent paramValue = factory.at(element.pos).Ident(elements.getName(p.snd));
                $buffer.append(paramValue);
            }

            if(p.fst==ParamType.PAIR){
                JCTree.JCLiteral paramName = factory.at(element.pos).Literal(p.snd);
                JCTree.JCIdent paramValue = factory.at(element.pos).Ident(elements.getName(p.snd));
                $buffer.append(paramName);
                $buffer.append(paramValue);
            }

        });



        JCTree.JCLiteral wholeMessage = factory.at(element.pos).Literal(message);
        $buffer.prepend(wholeMessage);

        JCTree.JCMethodInvocation callInfoMethod = factory.at(element.pos).Apply(com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerName)), elements.getName(logMethod)),
                $buffer.toList());

        return factory.at(element.pos).Exec(callInfoMethod);

    }

    public enum ParamType{
        PAIR,
        VAR;
    }
}
