package org.loguno.processor.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Dmitrii Ponomarev
 */
public class JCTreeUtilsTest {

    @Test
    public void testGetRepeatPart() throws Exception{
        final String message = "some message [x]";
        String repeatPart = JCTreeUtils.getRepeatPart(message);
        Assert.assertEquals("x",repeatPart);
    }
}
