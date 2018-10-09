package org.loguno.test;

import org.loguno.Loguno;

/**
 * @author Dmitrii Ponomarev
 */
@Loguno.Slf4j
public class PositiveCat {

    @Loguno
    public String sayIt(@Loguno String x){

        @Loguno.DEBUG("zxc")
        final int i = 0;

        @Loguno
        Object o = new Object();

        @Loguno
        String k = "";

        return k;

    }

}
