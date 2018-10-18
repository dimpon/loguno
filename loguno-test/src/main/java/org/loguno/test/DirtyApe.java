package org.loguno.test;

import org.loguno.Loguno;

@Loguno.Logger
public class DirtyApe extends Animal {


    @Loguno
    public DirtyApe(String a) {
        super(a);
    }

    @Loguno
    public void hiApe(int i){
        int u =2+i;
    }
}
