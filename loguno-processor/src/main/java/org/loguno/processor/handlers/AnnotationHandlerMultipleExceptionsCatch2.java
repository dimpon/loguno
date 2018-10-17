package org.loguno.processor.handlers;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CatchTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.ListBuffer;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.loguno.Loguno;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.utils.JCLogMethodBuilder;
import org.loguno.processor.utils.JCTreeUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerMultipleExceptionsCatch2<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

	public AnnotationHandlerMultipleExceptionsCatch2(JavacProcessingEnvironment environment) {
		super(environment);
	}

	@Handler
	@Order
	public static class AnnotationHandlerPipedExceptions
			extends AnnotationHandlerMultipleExceptionsCatch2<VoidAnnotation, PipedExceptions> {

		public AnnotationHandlerPipedExceptions(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(VoidAnnotation annotation, PipedExceptions element, ClassContext classContext) {


			final String e = element.varName;
			final CatchTree catchTree = element.element;
			final JCTree.JCBlock block = (JCTree.JCBlock)catchTree.getBlock();

			element.$exceptions.forEach((key, value) -> {

					if(value==null || value.isEmpty())
						return;


				final JCStatementHolder holder = JCStatementHolder.of()
						.element(block)
						.exceptionName(e);

				value.forEach(ann -> {
					JCTreeUtils.findHandlersAndCall(ann,holder,classContext);
				});





				JCTree.JCStatement body;

				if(holder.$logMethods.size()>1){
					body = factory.at(block.pos()).Block(0,holder.$logMethods.toList());
				}else {
					body = holder.$logMethods.first();

				}


				JCTree.JCIdent except = factory.Ident(elements.getName(e));

				JCTree.JCIf anIf = factory.at(block.pos())
						.If(factory.Parens(factory.TypeTest(except, key)), body, null);

				block.stats = block.stats.prepend(anIf);

			});



			// doRealJob(annotation.value(), method, element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerLoguno
			extends AnnotationHandlerMultipleExceptionsCatch2<Loguno, JCStatementHolder> {

		public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno annotation, JCStatementHolder element, ClassContext classContext) {
			String method = conf.getProperty(ConfigurationKeys.ERR_METHOD_DEFAULT);
			doRealJob(annotation.value(), method, element, classContext);
		}
	}

	void doRealJob(String[] value, String logMethod, JCStatementHolder holder, ClassContext classContext) {

		String message = JCTreeUtils.message(value, ConfigurationKeys.CATCH_MESSAGE_PATTERN_DEFAULT, classContext);

		String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

		JCTree.JCStatement methodCall = JCLogMethodBuilder.builder()
				.factory(factory)
				.elements(elements)
				.names(names)
				.element(holder.element)
				.loggerName(loggerVariable)
				.logMethod(logMethod)
				.message(message)
				.build()
				.addParam(holder.exceptionName)
				.create();

		holder.add(methodCall);

		/*JCTree.JCIdent except = factory.Ident(elements.getName(to.e));

		JCTree.JCIf anIf = factory.at(to.element.pos())
				.If(factory.Parens(factory.TypeTest(except, to.exceptionClass)), methodCall, null);

		to.body.stats = to.body.stats.prepend(anIf);*/

	}

	@NoArgsConstructor(staticName = "of")
	@Accessors(fluent = true, chain = true)
	@Setter
	public static class PipedExceptions {
		private String varName;
		private final Map<JCTree.JCExpression, List<? extends AnnotationTree>> $exceptions = new HashMap<>();
		private CatchTree element;

		public PipedExceptions putIfAbsentOneException(JCTree.JCExpression exception, List<? extends AnnotationTree> annotations) {
			this.$exceptions.putIfAbsent(exception, annotations);
			return this;
		}
	}

	@NoArgsConstructor(staticName = "of")
	@Accessors(fluent = true, chain = true)
	@Setter
	public static class JCStatementHolder {
		private JCTree element;
		private String exceptionName;

		ListBuffer<JCTree.JCStatement> $logMethods = new ListBuffer<>();

		void add(JCTree.JCStatement method) {
			$logMethods.append(method);
		}
	}
}
