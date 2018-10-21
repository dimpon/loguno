package org.loguno.test;

import org.junit.jupiter.api.Test;
import org.loguno.test.exceptions.FallDownException;
import org.loguno.test.exceptions.NoBananasException;
import org.slf4j.Logger;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;


public class JustMonkeyWithExceptionsTest {


    public Logger setLogger(Class<?> clazz) throws Exception {
        Field log = clazz.getDeclaredField("LOG");
        Logger mock = mock(Logger.class);
        Utils.setFinalStatic(log, mock);
        return mock;
    }

    @Test
    public void testDayFallDown() throws Exception {

        //Arrange
        Logger mock = setLogger(JustMonkeyWithExceptions.class);

        JustMonkeyWithExceptions monkey = JustMonkeyWithExceptions.of("Brikhead")
                .setEat(() -> {
                })
                .setJump(() -> {
                    throw new FallDownException();
                })
                .setTease(() -> {
                });

        //Act
        monkey.day1();

        //Assert
        verify(mock, times(1))
                .warn(eq("Exception is caught in org.loguno.test.JustMonkeyWithExceptions.day1."), any(FallDownException.class));

        verify(mock, times(1))
                .info(eq("Monkey hurt"), any(FallDownException.class));

    }

    @Test
    public void testDayNoBanana() throws Exception {

        //Arrange
        Logger mock = setLogger(JustMonkeyWithExceptions.class);

        JustMonkeyWithExceptions monkey = JustMonkeyWithExceptions.of("Brikhead")
                .setEat(() -> {
                    throw new NoBananasException();
                })
                .setJump(() -> {
                })
                .setTease(() -> {
                });

        //Act
        monkey.day1();

        //Assert
        verify(mock, times(1))
                .error(eq("Exception is caught in org.loguno.test.JustMonkeyWithExceptions.day1."), any(NoBananasException.class));

        verify(mock, times(1))
                .warn(eq("Monkey is hungry"), any(NoBananasException.class));

    }
}
