package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
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

    private Map<SupportedLoggers, Boolean> lazyLoggerIsGenerated = new EnumMap<>(SupportedLoggers.class);

    boolean isLazyLoggerHere(SupportedLoggers logger) {
        return lazyLoggerIsGenerated.getOrDefault(logger, false);
    }

    void lazyLoggerGenerated(SupportedLoggers logger) {
        lazyLoggerIsGenerated.put(logger, true);
    }

    @Getter
    @ToString
    @AllArgsConstructor(staticName = "of")
    public static class LoggerInfo {
        private final SupportedLoggers logger;
        private final String loggerName;
        private final boolean lazy;
    }


}
