package org.loguno.processor.handlers;

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


    private Map<Logger, Boolean> lazyLoggerIsGenerated = new EnumMap<>(Logger.class);

    public boolean isLoggerGenerated(Logger logger) {
        return lazyLoggerIsGenerated.getOrDefault(logger, false);
    }

    public void loggerIsGenerated(Logger logger) {
        lazyLoggerIsGenerated.put(logger, true);
    }


    public enum Logger {
        Slf4j,
        Log4j,
        Log4j2;
    }
}
