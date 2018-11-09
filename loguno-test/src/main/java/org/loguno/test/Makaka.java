package org.loguno.test;

import lombok.*;
import org.loguno.Loguno;
import org.loguno.processor.handlers.Frameworks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@org.loguno.Loguno.Logger(lazy = true, name = "LOGGER", value = Frameworks.SLF4J)
// @org.loguno.Loguno({"m", "n", "l"})
// @Loguno({"P"})
// @Loguno()
// @Loguno("aaa")
// @org.loguno.Loguno.INFO()
// @NoArgsConstructor(onConstructor_ = @Loguno)
// @Getter(onMethod_ = { @Loguno, @Loguno.TRACE })
// @Setter(onMethod_ = { @Loguno, @Loguno.TRACE })
public class Makaka extends Animal {

	private String ccc;

	/*
	 * @Loguno
	 * public Makaka(String a) {
	 * super(a);
	 * }
	 */

	/*
	 * @Loguno
	 * 
	 * @Loguno.TRACE
	 * 
	 * @Loguno.DEBUG("xui s gori")
	 * public Makaka() {
	 * super();
	 * }
	 * 
	 * @Loguno
	 * 
	 * @org.loguno.Loguno.ERROR
	 * private String hiDo(@Loguno String param1, @org.loguno.Loguno.TRACE @Loguno.DEBUG("AAAA") String param2) {
	 * 
	 * @Loguno
	 * 
	 * @org.loguno.Loguno.TRACE
	 * 
	 * @Loguno.DEBUG("sss")
	 * String val = "bad";
	 * 
	 * return val;
	 * 
	 * }
	 */

	@Loguno @Loguno.INFO @Loguno.TRACE @Loguno.DEBUG @Loguno.ERROR @Loguno.WARN
	public void dodo(@Loguno @Loguno.INFO @Loguno.TRACE @Loguno.DEBUG @Loguno.ERROR @Loguno.WARN String a1, @Loguno String a2) {

		final String localVar = "hi";

		List<String> aa = new ArrayList();

		for (@Loguno String o : aa) {
			System.out.printf(o);
		}


		for (@Loguno int i = 0; i < 10; i++) {
			a1 = a1 + i;

			final String zoo = "hi";

		}

		try{

		}catch(Exception eee){

		}

		final Object obj;


		int k = 0;

	}

}
