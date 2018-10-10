package org.loguno.processor.handlers;

import com.sun.source.tree.BlockTree;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
public class ClassContext {

    private Logger logger;
    private String loggerName;
    private boolean lazy;


    private BlockTree currentBlock;


    private Map<Logger, Boolean> lazyLoggerIsGenerated = new EnumMap<>(Logger.class);

    public boolean isLoggerGenerated(Logger logger) {
        return lazyLoggerIsGenerated.getOrDefault(logger, false);
    }

    public void generated(Logger logger) {
        lazyLoggerIsGenerated.put(logger, true);
    }


    public enum Logger {
        Slf4j,
        Log4j,
        Log4j2;
    }
}
