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
        JustMonkey ape = new JustMonkey("Gumpa");

        //Assert
        verify(mock, times(1))
                .info("org.loguno.test.JustMonkey.<init> is invoked.{}:{}", "a", "Gumpa");

        verify(mock, times(1))
                .info("org.loguno.test.JustMonkey.<init> method param {}={}", "a", "Gumpa");

        ape.hiApe(2);

        verify(mock, times(1))
                .info("org.loguno.test.JustMonkey.hiApe is invoked.{}:{}", "i", 2);

        verify(mock, times(1))
                .info("local variable {}={}", "u", 4);

    }




}
