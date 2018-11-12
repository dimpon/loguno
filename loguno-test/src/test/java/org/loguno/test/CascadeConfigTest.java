package org.loguno.test;

import org.junit.jupiter.api.Test;
import org.loguno.test.paka.one.One;
import org.loguno.test.paka.two.Two;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CascadeConfigTest {

    public Logger setLogger(Class<?> clazz) throws Exception {
        Field log = clazz.getDeclaredField("LOG");
        Logger mock = mock(Logger.class);
        Utils.setFinalStatic(log, mock);
        return mock;
    }

    @Test
    public void testOne() throws Exception {

        //Arrange
        Logger mock = setLogger(One.class);

        //Act
        One o = new One();
        o.doSmth("some");

        //Assert
        verify(mock, times(1))
                .info("Just Method parameter {}={}", "p", "some");

        verify(mock, times(1))
                .info("Just Method is called. Hokuspokus! Parameter {}={}", "p", "some");


        verify(mock, times(1))
                .info("One variable {}={}", "z", "SOME");


    }

    @Test
    public void testTwo() throws Exception {

        //Arrange
        Logger mock = setLogger(Two.class);

        //Act
        Two t = new Two();
        t.doSmth("some");

        //Assert
        verify(mock, times(1))
                .info("Just Method parameter {}={}", "p", "some");

        verify(mock, times(1))
                .info("Just Method is called. Hokuspokus! Parameter {}={}", "p", "some");


        verify(mock, times(1))
                .info("Two variable {}={}", "z", "SOME");
    }
}
