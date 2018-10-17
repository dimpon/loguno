package org.loguno.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class PositiveCatTest {

   /* @Test(expected = SecurityException.class)
    public void testSingletonGetter() throws Exception {
        Unsafe.getUnsafe();
    }*/

    @Test
    public void testPositiveCat() {


        HelloKitty k = new HelloKitty();

        k.sayHello();

        k.sayName("Barsik").sayOwner("Vovochka");

        //PositiveCat cat = new PositiveCat();
        //cat.sayIt("hello","Kitty");

      /*  org.slf4j.SupportedLoggers log = mock(org.slf4j.SupportedLoggers.class);
        PowerMockito.mockStatic(LoggerFactory.class);

        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(log);

        PositiveDog dog = new PositiveDog();

        System.out.println("******** dog is created");


        dog.barking("aaa", "bbb");

        System.out.println("******** dog is barking");

        dog.miu();
        dog.miu(12);

        verify(log, times(1))
                .info("{}.{}() is invoked.{}:{},{}:{}", "PositiveDog", "barking", "a1111", "aaa", "a2222", "bbb");

        verify(log, times(2))
                .info("{}.{}() is invoked.", "PositiveDog", "miu");

        verify(log, times(1))
                .info("{}.{}() is invoked.{}:{}", "PositiveDog", "miu", "ka", 12);*/

    }
}
