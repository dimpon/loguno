package org.loguno.processor.utils;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Names;
import lombok.Builder;

@Builder
public class JCLogMethodBuilder {

    private final TreeMaker factory;

    private final Names names;

    private final JavacElements elements;

    private final String message;

    private final String loggerName;

    private final String logMethod;

    private final JCTree element;

    private final ListBuffer<JCTree.JCExpression> $buffer = new ListBuffer<>();

    public JCLogMethodBuilder addParamPair(String name) {
        JCTree.JCLiteral paramName = factory.at(element.pos).Literal(name);
        JCTree.JCIdent paramValue = factory.at(element.pos).Ident(elements.getName(name));
        $buffer.append(paramName);
        $buffer.append(paramValue);
        return this;
    }

    public JCLogMethodBuilder addParam(String name) {
        JCTree.JCIdent paramValue = factory.at(element.pos).Ident(elements.getName(name));
        $buffer.append(paramValue);
        return this;
    }

    public JCTree.JCStatement create() {

        JCTree.JCLiteral wholeMessage = factory.at(element.pos).Literal(message);
        $buffer.prepend(wholeMessage);

        JCTree.JCMethodInvocation callInfoMethod = factory.at(element.pos).Apply(List.<JCTree.JCExpression>nil(),
                factory.Select(factory.Ident(elements.getName(loggerName)), elements.getName(logMethod)),
                $buffer.toList());

        return factory.at(element.pos).Exec(callInfoMethod);

    }
}
