package org.loguno.processor.utils.annotations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.processor.handlers.VoidAnnotation;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Ponomarev
 */
@NoArgsConstructor
public class VoidAnnotationImpl implements VoidAnnotation {
    @Override
    public Class<? extends Annotation> annotationType() {
        return VoidAnnotation.class;
    }
}
