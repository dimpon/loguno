package org.loguno.test;

import lombok.*;
import org.loguno.Loguno;
import org.loguno.processor.handlers.Frameworks;

@org.loguno.Loguno.Logger(lazy = true, name = "LOGGER", value = Frameworks.SLF4J)
//@org.loguno.Loguno({"m", "n", "l"})
//@Loguno({"P"})
//@Loguno()
//@Loguno("aaa")
//@org.loguno.Loguno.INFO()
@NoArgsConstructor(onConstructor_ = @Loguno)
@Getter(onMethod_ = {@Loguno,@Loguno.TRACE})
@Setter(onMethod_ = {@Loguno,@Loguno.TRACE})
public class Makaka extends Animal{


    private String ccc;

    @Loguno
    public Makaka(String a) {
        super(a);
    }

  /*  @Loguno
    @Loguno.TRACE
    @Loguno.DEBUG("xui s gori")
    public Makaka() {
        super();
    }

    @Loguno
    @org.loguno.Loguno.ERROR
    private String hiDo(@Loguno String param1, @org.loguno.Loguno.TRACE @Loguno.DEBUG("AAAA") String param2) {

        @Loguno
        @org.loguno.Loguno.TRACE
        @Loguno.DEBUG("sss")
        String val = "bad";

        return val;

    }*/
}
