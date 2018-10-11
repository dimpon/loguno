package org.loguno.test;

import org.loguno.Loguno;
import org.loguno.processor.handlers.ClassContext;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Slf4j(lazy = true)
public class PositiveCat extends Animal {

	String ccc;

	@Loguno
	public PositiveCat() {
		super("cat");
		ccc = "xoxox";
		dodo();
	}

	@Loguno("1")
	public String sayIt(@Loguno("2") String x, String x1) throws @Loguno("3") NumberFormatException {

		/*
		 * @Loguno.DEBUG(value = {"zxc", "abc"},
		 * context = true,
		 * string = "toString()",
		 * logger = ClassContext.Logger.Slf4j,
		 * clazz = Number.class,
		 * aaaa = {Override.class, Loguno.class},
		 * iii = {1, 2, 3, 4, 5},
		 * num = 555)
		 */

		@Loguno.DEBUG(value = "AAA")
		@Loguno.INFO("info me")
		final int xx = 5;

		dodo();

		@Loguno.DEBUG("4")
		Object o = new Object();

		for (int i = 0; i < 10; i++) {
			@Loguno.DEBUG("9")
			String kkk = "";
		}

		@Loguno("5")
		String k = "";

		int i1 = Integer.parseInt("1");

		return k;

	}

	@Loguno
	private void dodo() {
		System.out.printf("dodo");
	}

}
