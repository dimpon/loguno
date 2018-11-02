package org.loguno.processor.utils.annotations;

import lombok.Getter;
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
@RequiredArgsConstructor(staticName = "of")
public class LogunoLoggerImpl implements Loguno.Logger {

    private Frameworks value= Frameworks.NONE;
    private String name="LOG";
    private boolean lazy= false;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Loguno.Logger.class;
    }
}
