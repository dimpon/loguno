package org.loguno.test;


import org.loguno.Loguno;
import org.loguno.processor.handlers.ClassContext;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Slf4j(value = "LOGGER", lazy = true)
public class PositiveCat extends Animal {

    private String ccc;

    @Loguno
    public PositiveCat() {
        super("cat");
        ccc = "xoxox";
        dodo(new Date(), "Eduard");
    }

    @Loguno
    public PositiveCat(@Loguno String catName,@Loguno("gender = {}") String catGender) {
        super(catName);
    }

    @Loguno
    @Loguno.TRACE("Custom message. Has only {method}.")
    public String sayIt(String x, String x1) throws @Loguno("3") NumberFormatException {

        @Loguno
        @Loguno final String xx = "kalligraphy";

        dodo(new Date(), "Eduard");

        @Loguno
        Object o = new Object();

        for (int i = 0; i < 10; i++) {
            @Loguno("99999")
            String kkk = "";
        }

        @Loguno("Variable K")
        String k = "";

        int i1 = Integer.parseInt("1");

        return k + xx;

    }

    @Loguno("i'm in!!!!")
    @Loguno.DEBUG
    @Loguno.INFO
    @Loguno.ERROR
    @Loguno.TRACE("Run Lola run! This is class {class}.")
    @Loguno.WARN
    private void dodo(Date date, String nameOfKing) {
        System.out.printf("dodo");
    }

    private void dododo(@Loguno String x1, @Loguno("abc") String x2, String x3, String x4) {
        System.out.printf("dodo");
    }

}
