package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.tree.JCTree;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
@ToString
public class ClassContext {

    private LinkedList<LoggerInfo> loggers = new LinkedList<>();
    private LinkedList<String> classes = new LinkedList<>();
    private LinkedList<String> methods = new LinkedList<>();
    private LinkedList<BlockTree> blocks = new LinkedList<>();
    private LinkedList<MethodTree> methodsBlocks = new LinkedList<>();

    private LinkedList<JCTree> breadcrumb = new LinkedList<>();

    private Map<Frameworks, Boolean> lazyLoggerIsGenerated = new EnumMap<>(Frameworks.class);

    boolean isLazyLoggerHere(Frameworks logger) {
        return lazyLoggerIsGenerated.getOrDefault(logger, false);
    }

    void lazyLoggerGenerated(Frameworks logger) {
        lazyLoggerIsGenerated.put(logger, true);
    }

    public JCTree getVarOwner(JCTree.JCVariableDecl var) {
        if (breadcrumb.size() < 2)
            throw new RuntimeException("Local variable path is wrong "+var);

        int i = breadcrumb.indexOf(var);
        return breadcrumb.get(i-1);
    }


    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    public static class LoggerInfo {
        private final Frameworks logger;
        private final String loggerName;
        private final boolean lazy;
    }


}
