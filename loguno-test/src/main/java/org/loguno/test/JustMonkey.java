package org.loguno.test;

import org.loguno.Loguno;

@Loguno.Logger
public class JustMonkey extends Animal {


    @Loguno
    public JustMonkey(@Loguno String a) {
        super(a);
    }

    @Loguno
    public void hiApe(int i){
        @Loguno
        int u =2+i;
    }
}
