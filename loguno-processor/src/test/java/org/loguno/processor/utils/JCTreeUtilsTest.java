package org.loguno.processor.utils;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.Frameworks;

import javax.lang.model.element.ExecutableElement;

/**
 * @author Dmitrii Ponomarev
 */
public class JCTreeUtilsTest {

	@Test
	public void testGetRepeatPart() throws Exception {
		final String m1 = "some message [x]";
		String r1 = JCTreeUtils.getRepeatPart(m1);
		Assertions.assertEquals("x", r1);

		final String m2 = "just message";
		String r2 = JCTreeUtils.getRepeatPart(m2);
		Assertions.assertEquals("", r2);
	}

	@Test
	public void testStandardConfiguration() throws Exception {
		Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

		String logMethod = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
		Assertions.assertEquals("info", logMethod);

		String errMethod = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
		Assertions.assertEquals("error", errMethod);

		Frameworks framework = conf.getProperty(ConfigurationKeys.LOGGING_FRAMEWORK_DEFAULT);
		Assertions.assertEquals(Frameworks.SLF4J, framework);

		Boolean enable = conf.getProperty(ConfigurationKeys.ENABLE);
		Assertions.assertTrue(enable);
	}

	@Test
	public void testHasBody() throws Exception {

		JCTree.JCBlock block = new JCTree.JCBlock(0, null) {
		};

		class Method extends JCTree.JCMethodDecl {
            public Method(JCModifiers jcModifiers, Name name, JCExpression jcExpression, List<JCTypeParameter> list, JCVariableDecl jcVariableDecl, List<JCVariableDecl> list1, List<JCExpression> list2, JCBlock jcBlock, JCExpression jcExpression1, Symbol.MethodSymbol methodSymbol) {
                super(jcModifiers, name, jcExpression, list, jcVariableDecl, list1, list2, jcBlock, jcExpression1, methodSymbol);
            }
        };
		Assertions.assertFalse(JCTreeUtils.hasBody(block));
	}
}
