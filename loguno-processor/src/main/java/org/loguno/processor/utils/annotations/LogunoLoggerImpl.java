package org.loguno.processor.utils.annotations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.Loguno;
import org.loguno.processor.handlers.Frameworks;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Ponomarev
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class LogunoLoggerImpl implements Loguno.Logger {

    private Frameworks value;
    private String name;
    private boolean lazy;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Loguno.Logger.class;
    }
}
