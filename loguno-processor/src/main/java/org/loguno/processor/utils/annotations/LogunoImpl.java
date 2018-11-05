package org.loguno.processor.utils.annotations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.Loguno;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Ponomarev
 */
@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
public class LogunoImpl implements Loguno {
    private String[] value;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Loguno.class;
    }
}
