package org.loguno.processor.handlers;

import com.sun.source.tree.AnnotationTree;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dmitrii Ponomarev
 */
public abstract class AnnotationHandlerPipedExceptionsCatch<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

	public AnnotationHandlerPipedExceptionsCatch(JavacProcessingEnvironment environment) {
		super(environment);
	}

	@Handler
	@Order
	public static class AnnotationHandlerPipedExceptions
			extends AnnotationHandlerPipedExceptionsCatch<VoidAnnotation, PipedExceptionsHolder> {

		public AnnotationHandlerPipedExceptions(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(VoidAnnotation annotation, PipedExceptionsHolder peHolder, ClassContext classContext) {

			final String e = peHolder.varName;
			final CatchTree catchTree = peHolder.element;
			final JCTree.JCBlock block = (JCTree.JCBlock)catchTree.getBlock();

			peHolder.$exceptions.forEach((key, value) -> {

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
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerDebug extends AnnotationHandlerPipedExceptionsCatch<Loguno.DEBUG, JCStatementHolder> {

		public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.DEBUG annotation, JCStatementHolder element, ClassContext classContext) {
			doRealJob(annotation.value(), "debug", element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerLoguno extends AnnotationHandlerPipedExceptionsCatch<Loguno, JCStatementHolder> {

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
	}

	@NoArgsConstructor(staticName = "of")
	@Accessors(fluent = true, chain = true)
	@Setter
	public static class PipedExceptionsHolder {
		private String varName;
		private final Map<JCTree.JCExpression, List<? extends AnnotationTree>> $exceptions = new HashMap<>();
		private CatchTree element;

		public PipedExceptionsHolder putIfAbsentOneException(JCTree.JCExpression exception, List<? extends AnnotationTree> annotations) {
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