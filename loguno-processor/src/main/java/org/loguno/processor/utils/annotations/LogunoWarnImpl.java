package org.loguno.processor.utils.annotations;

import lombok.Getter;
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
@RequiredArgsConstructor(staticName = "of")
public class LogunoWarnImpl implements Loguno.WARN {
	private String[] value;

	@Override
	public Class<? extends Annotation> annotationType() {
		return Loguno.WARN.class;
	}
}
