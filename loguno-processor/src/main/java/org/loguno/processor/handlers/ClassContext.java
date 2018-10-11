package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
public class ClassContext {

    private Logger logger;
    private String loggerName;
    private boolean lazy;

    private LinkedList<String> classes = new LinkedList<>();
    private LinkedList<String> methods = new LinkedList<>();

    private BlockTree currentBlock;

    private Map<Logger, Boolean> lazyLoggerIsGenerated = new EnumMap<>(Logger.class);

    boolean isLoggerHere(Logger logger) {
        return lazyLoggerIsGenerated.getOrDefault(logger, false);
    }

    void generated(Logger logger) {
        lazyLoggerIsGenerated.put(logger, true);
    }


    public enum Logger {
        Slf4j,
        Log4j,
        Log4j2;
    }
}
