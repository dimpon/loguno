package org.loguno.test;


import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

public class JustMonkeyTest {


    @Test
    public void testDirtyApe() throws Exception {

        //Arrange
        Field log = JustMonkey.class.getDeclaredField("LOG");
        Logger mock = mock(Logger.class);
        Utils.setFinalStatic(log, mock);

        //Act
        JustMonkey ape = new JustMonkey("Gumpa","Gambiya");

        //Assert
        verify(mock, times(1))
                .info("org.loguno.test.JustMonkey.<init> Method parameter {}={}", "a", "Gumpa");

        verify(mock, times(1))
                .info("Just Method is called. Hokuspokus! Parameter {}={},Parameter {}={}", "a", "Gumpa", "b", "Gambiya");

        ape.hiApe(2);

        verify(mock, times(1))
                .info("Just Method is called. Hokuspokus! Parameter {}={}", "i", 2);

        verify(mock, times(1))
                .info("org.loguno.test.JustMonkey.hiApe Local variable {}={}", "u", 4);
    }
}
