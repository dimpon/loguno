package org.loguno.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LoggerFactory.class)
public class PositiveDogTest {
    @Test
    public void testPositiveDog() {

        org.slf4j.Logger log = mock(org.slf4j.Logger.class);
        PowerMockito.mockStatic(LoggerFactory.class);

        when(LoggerFactory.getLogger(any(Class.class))).thenReturn(log);

        PositiveDog dog = new PositiveDog();
        dog.barking("aaa", "bbb");
        dog.miu();
        dog.miu(12);

        verify(log, times(1))
                .info("{}.{}() is invoked.{}:{},{}:{}", "PositiveDog", "barking", "a1111", "aaa", "a2222", "bbb");

        verify(log, times(2))
                .info("{}.{}() is invoked.", "PositiveDog", "miu");

        verify(log, times(1))
                .info("{}.{}() is invoked.{}:{}", "PositiveDog", "miu", "ka", 12);

    }
}
