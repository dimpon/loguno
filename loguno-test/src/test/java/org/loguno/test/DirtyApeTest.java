package org.loguno.test;


import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
        setFinalStatic(log,mock);

        //Act
        DirtyApe ape = new DirtyApe("Gumpa");

        ape.hiApe(2);

        //Assert
        verify(mock, times(1))
                .info(eq("org.loguno.test.DirtyApe.<init>() is invoked with success. {}:{}"), eq("a"), eq("Gumpa"));

        verify(mock, times(1))
                .info(eq("org.loguno.test.DirtyApe.hiApe() is invoked with success. {}:{}"), eq("i"), eq(2));
    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
