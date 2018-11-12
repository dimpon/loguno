package org.loguno.test;

import lombok.*;
import org.loguno.Loguno;
import org.loguno.processor.handlers.Frameworks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@org.loguno.Loguno.Logger(lazy = true, name = "LOGGER", value = Frameworks.SLF4J)
// @org.loguno.Loguno({"m", "n", "l"})
// @Loguno({"P"})
// @Loguno()
// @Loguno("aaa")
// @org.loguno.Loguno.INFO()
//@NoArgsConstructor(onConstructor_ = @Loguno)
//@Getter(onMethod_ = {@Loguno, @Loguno.TRACE})
//@Setter(onMethod_ = {@Loguno, @Loguno.TRACE})
public class Makaka extends Animal {

    private String ccc;

    @Loguno
    public Makaka(String a, String ccc) {
        super(a);
        this.ccc = ccc;
    }

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

    public String someMe() throws @Loguno @Loguno.TRACE @Loguno.WARN IllegalArgumentException,  NullPointerException{
        try {
            aaa();
        } catch (@Loguno NullPointerException | @Loguno.ERROR IllegalArgumentException e) {

        } catch (@Loguno Exception eee) {

        }

        return "ok";
    }

    @Loguno
    @Loguno.TRACE
    public void dodo(@Loguno.ERROR @Loguno.WARN String a1, String a2)  {

        final String localVar = "hi";

        List<String> aa = new ArrayList();

        for (@Loguno String o : aa) {
            System.out.printf(o);
            System.out.printf(o);
        }

        IntStream.range(1, 10).forEach(value -> {

            @Loguno
            int buba = 6;

        });


        @Loguno
        Function<String, String> fun = (a) -> {
            @Loguno
            int counter = 6;
            return a;
        };


        for (@Loguno int i = 0; i < 10; i++) {
            System.out.printf(i + "");

            final String zoo = "hi";

        }



        try (@Loguno Stream<String> lines = aa.stream()) {

        }

       /* @Loguno.TRACE
        @Loguno.INFO
        final Object obj=new Object();


        @Loguno.TRACE
        @Loguno.WARN
        int k = 0;*/

    }

    private void aaa() throws NullPointerException, IllegalArgumentException {

    }

}
