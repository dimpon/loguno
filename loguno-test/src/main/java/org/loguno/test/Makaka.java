package org.loguno.test;

import lombok.Getter;
import org.loguno.Loguno;
import org.loguno.processor.handlers.Frameworks;

@org.loguno.Loguno.Logger(lazy = true, value = Frameworks.SLF4J)
@org.loguno.Loguno({"m","n","l"})
//@Loguno({"P"})
//@Loguno()
@Loguno("aaa")
//@org.loguno.Loguno.INFO()
public class Makaka {

    @Getter(onMethod_ = {@Loguno,@Loguno.TRACE})
    private String ccc;

    @Loguno
    @Loguno
    @Loguno.DEBUG("xui s gori")
    public Makaka() {
        super();
    }

    //@Loguno
   // @org.loguno.Loguno.ERROR
    private String hiDo(@Loguno String param1,@org.loguno.Loguno.TRACE  @Loguno.DEBUG("AAAA") String param2) {

        @Loguno
        @org.loguno.Loguno.TRACE
        @Loguno.DEBUG("sss")
        String val = "bad";

        return val;

    }
}
