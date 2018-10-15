package org.loguno.test;


import org.loguno.Loguno;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.SupportedLoggers;

import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.zip.DataFormatException;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Logger(name="LOOGER",lazy = true)//(name = "LOGGER", lazy = true)
//@Loguno.Logger(SupportedLoggers.Slf4j);
public class PositiveCat extends Animal {

    private String ccc;



    @Loguno
    public PositiveCat() {
        super("cat");
        ccc = "xoxox";
    }

    @Loguno
    public PositiveCat(@Loguno String catName, @Loguno("gender = {}") String catGender) {
        super(catName);
    }

    //@Loguno
    //@Loguno.TRACE("Custom message. Has only {method}.")
    public String sayIt(String x, String x1) {

       // @Loguno
        @Loguno("hren' kakaeto")
        final String xx = "kalligraphy";

        dodo(new Date(), "Eduard");

        //@Loguno
        Object o = new Object();

        for (int i = 0; i < 10; i++) {
            //@Loguno("99999")
            String kkk = "";
        }

        //@Loguno("Variable K")
        String k = "";
        try {
            int i1 = Integer.parseInt("1");
        } catch (@Loguno("all is broken") NumberFormatException | NullPointerException e) {
            e.printStackTrace();
        }
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

    @Loguno.Logger
    private static class RightLeg{

    }

}
