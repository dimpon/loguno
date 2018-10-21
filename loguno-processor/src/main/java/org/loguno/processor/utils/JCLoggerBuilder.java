package org.loguno.processor.utils;

import com.sun.source.tree.ClassTree;
import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;
import lombok.Builder;
import lombok.Singular;

import java.util.List;

@Builder
public class JCLoggerBuilder {

    @Singular
    private final List<Integer> modifiers;

    private final TreeMaker factory;

    private final Names names;

    private final JavacElements elements;

    private final String loggerName;

    private final String loggerClass;

    private final String factoryClassAndMethod;

    private final ClassTree classTree;

    private final String topLevelClass;

    public JCTree.JCVariableDecl create() {

        JCTree.JCModifiers jcModifiers = factory.Modifiers(modifiers.stream().reduce(0, (i, f) -> i | f));
        Name logger = names.fromString(loggerName);
        Name topLevelClassName = elements.getName(topLevelClass);
        JCTree.JCFieldAccess topLevenElement = factory.Select(factory.Ident(topLevelClassName), elements.getName("class"));

        JCExpressionBuilder expressionBuilder = JCExpressionBuilder.builder()
                .elements(elements)
                .factory(factory)
                .build();

        JCTree.JCExpression loggerType = expressionBuilder.createJCExpression(loggerClass);
        JCTree.JCExpression method = expressionBuilder.createJCExpression(factoryClassAndMethod);

        JCTree.JCMethodInvocation factoryMethodCall =
                factory.Apply(com.sun.tools.javac.util.List.<JCTree.JCExpression>nil(),
                        method, com.sun.tools.javac.util.List.<JCTree.JCExpression>of(topLevenElement));

        return factory.at(((JCTree) classTree).pos).VarDef(jcModifiers, logger, loggerType, factoryMethodCall);

    }
}
