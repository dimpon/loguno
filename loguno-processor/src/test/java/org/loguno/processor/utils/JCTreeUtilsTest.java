package org.loguno.processor.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationImpl;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.SupportedLoggers;

/**
 * @author Dmitrii Ponomarev
 */
public class JCTreeUtilsTest {

    @Test
    public void testGetRepeatPart() throws Exception{
        final String m1 = "some message [x]";
        String r1 = JCTreeUtils.getRepeatPart(m1);
        Assertions.assertEquals("x",r1);

        final String m2 = "just message";
        String r2 = JCTreeUtils.getRepeatPart(m2);
        Assertions.assertEquals("",r2);
    }

    @Test
    public void testStandardConfiguration() throws Exception{
        Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

        String logMethod = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
        Assertions.assertEquals("info",logMethod);

        String errMethod = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
        Assertions.assertEquals("error",errMethod);

        SupportedLoggers framework = conf.getProperty(ConfigurationKeys.LOGGING_FRAMEWORK_DEFAULT);
        Assertions.assertEquals(SupportedLoggers.SLF4J,framework);

        Boolean enable = conf.getProperty(ConfigurationKeys.ENABLE);
        Assertions.assertTrue(enable);
    }
}
