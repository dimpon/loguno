package org.loguno.test;


import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

public class DirtyApeTest {


    @Test
    public void testDirtyApe() throws Exception {

        //Arrange
        Field log = DirtyApe.class.getDeclaredField("LOG");
        Logger mock = mock(Logger.class);
        setFinalStatic(log, mock);

        //Act
        DirtyApe ape = new DirtyApe("Gumpa");

        ape.hiApe(2);

        verify(mock, times(1))
                .info("org.loguno.test.DirtyApe.<init>() is invoked.{}:{}", "a", "Gumpa");

        verify(mock, times(1))
                .info("org.loguno.test.DirtyApe.hiApe() is invoked with success. {}:{}", "i", 2);
    }

    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
