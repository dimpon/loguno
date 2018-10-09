package org.loguno.test;

import org.loguno.Loguno;

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

        @Loguno.DEBUG("zxc")
        final int i = 0;

        @Loguno("4")
        Object o = new Object();

        @Loguno("5")
        String k = "";

        int i1 = Integer.parseInt("1");

        return k;

    }

    public static void main(String[] args) throws Exception {

        Class<Loguno.DEBUG> clazz = Loguno.DEBUG.class;

        Constructor<?>[] constructors = clazz.getConstructors();

        Loguno.DEBUG debug = clazz.newInstance();



        System.out.printf(""+debug);


    }

}
