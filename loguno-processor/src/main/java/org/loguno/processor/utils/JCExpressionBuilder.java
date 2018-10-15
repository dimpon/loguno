package org.loguno.processor.utils;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Builder;

@Builder
public class JCExpressionBuilder {

    private final TreeMaker factory;

    private final JavacElements elements;

    public JCTree.JCExpression createJCExpression(final String fullName) {
        String[] splitted = fullName.split("\\.");
        return doRound(splitted, (splitted.length - 1));
    }

    private JCTree.JCExpression doRound(String[] splitted, int i) {
        if (i == 0)
            return factory.Ident(elements.getName(splitted[i]));
        return factory.Select(doRound(splitted, i - 1), elements.getName(splitted[i]));
    }

}
