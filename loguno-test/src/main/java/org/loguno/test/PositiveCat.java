package org.loguno.test;

import org.loguno.Loguno;
import org.loguno.processor.handlers.ClassContext;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Slf4j("LOGGER")
public class PositiveCat {

    @Loguno("1")
    public String sayIt(@Loguno("2") String x) throws @Loguno("3") NumberFormatException {

        @Loguno.DEBUG(value = "zxc",string = "toString"/*, context = true, string = "toString()", logger = ClassContext.Logger.Slf4j*/)
        @Loguno("8")
        @Loguno.INFO("info me")
        final int xx = 0;

        dodo();

        @Loguno("4")
        Object o = new Object();

        for (int i = 0; i < 10; i++) {
            System.out.println(o.toString());
        }

        @Loguno("5")
        String k = "";

        int i1 = Integer.parseInt("1");

        return k;

    }

    private void dodo() {
        System.out.printf("dodo");
    }

    public static void main(String[] args) throws Exception {

        Class<Loguno.DEBUG> clazz = Loguno.DEBUG.class;

        Constructor<?>[] constructors = clazz.getConstructors();

        Loguno.DEBUG debug = clazz.newInstance();


        System.out.printf("" + debug);


    }

}
