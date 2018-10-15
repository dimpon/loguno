package org.loguno.processor.handlers;

import com.sun.tools.javac.util.ListBuffer;
import org.loguno.Loguno;

import org.loguno.processor.configuration.ConfigurationKeys;
import com.sun.source.tree.MethodTree;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import org.loguno.processor.utils.JCTreeUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.loguno.processor.configuration.ConfigurationKeys.*;
import static org.loguno.processor.utils.JCTreeUtils.*;

public abstract class AnnotationHandlerMethod<A extends Annotation, E> extends AnnotationHandlerBase<A, E> {

	protected AnnotationHandlerMethod(JavacProcessingEnvironment environment) {
		super(environment);
	}

	@Handler
	@Order
	public static class AnnotationHandlerLoguno extends AnnotationHandlerMethod<Loguno, ExecutableElement> {

		public AnnotationHandlerLoguno(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno annotation, ExecutableElement element, ClassContext classContext) {
			String method = conf.getProperty(ConfigurationKeys.LOG_METHOD_DEFAULT);
			doRealJob(annotation.value(), method, element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerDebug extends AnnotationHandlerMethod<Loguno.DEBUG, ExecutableElement> {

		public AnnotationHandlerDebug(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.DEBUG annotation, ExecutableElement element, ClassContext classContext) {
			doRealJob(annotation.value(), "debug", element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerInfo extends AnnotationHandlerMethod<Loguno.INFO, ExecutableElement> {

		public AnnotationHandlerInfo(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.INFO annotation, ExecutableElement element, ClassContext classContext) {
			doRealJob(annotation.value(), "info", element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerError extends AnnotationHandlerMethod<Loguno.ERROR, ExecutableElement> {

		public AnnotationHandlerError(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.ERROR annotation, ExecutableElement element, ClassContext classContext) {
			doRealJob(annotation.value(), "error", element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerTrace extends AnnotationHandlerMethod<Loguno.TRACE, ExecutableElement> {

		public AnnotationHandlerTrace(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.TRACE annotation, ExecutableElement element, ClassContext classContext) {
			doRealJob(annotation.value(), "trace", element, classContext);
		}
	}

	@Handler
	@Order
	public static class AnnotationHandlerWarn extends AnnotationHandlerMethod<Loguno.WARN, ExecutableElement> {

		public AnnotationHandlerWarn(JavacProcessingEnvironment environment) {
			super(environment);
		}

		@Override
		public void processTree(Loguno.WARN annotation, ExecutableElement element, ClassContext classContext) {
			doRealJob(annotation.value(), "warn", element, classContext);
		}
	}

	void doRealJob(String[] value, String logMethod, ExecutableElement element, ClassContext classContext) {

		MethodTree methodTree = trees.getTree(element);

		// generate array of pairs - param name-param value
		JCTree.JCExpression[] idents = methodTree.getParameters().stream()
				.map(param -> Stream.<JCTree.JCExpression> of(
						factory.Literal(param.getName().toString()),
						factory.Ident(elements.getName(param.getName()))))
				.flatMap(s -> s)
				.toArray(JCTree.JCExpression[]::new);

		String message = JCTreeUtils.tryToInsertClassAndMethodName(getMessageTemplate(value, METHOD_MESSAGE_PATTERN_DEFAULT), classContext);

		final String paramsTemplate = paramSuffix(message);

		message = message.replaceAll("\\[(.*?)\\]", "");

		if (!paramsTemplate.isEmpty()) {
			String paramsStr = methodTree.getParameters().stream().map(o -> paramsTemplate).collect(Collectors.joining(","));
			message = message + paramsStr;
		}

		JCTree.JCLiteral wholeMessage = factory.Literal(message);

		String loggerVariable = classContext.getLoggers().getLast().getLoggerName();

		final ListBuffer<JCTree.JCExpression> buffer = new ListBuffer<>();
		buffer.append(wholeMessage);

		if (message.contains("{}")) {
			Arrays.stream(idents).forEach(buffer::append);
		}

		JCTree.JCMethodInvocation callInfoMethod = factory.Apply(List.<JCTree.JCExpression> nil(),
				factory.Select(factory.Ident(elements.getName(loggerVariable)), elements.getName(logMethod)),
				buffer.toList());

		JCTree.JCStatement callInfoMethodCall = factory.at(((JCTree) methodTree).pos).Exec(callInfoMethod);

		JCTree.JCBlock body = (JCTree.JCBlock) methodTree.getBody();

		if (element.getKind() == ElementKind.CONSTRUCTOR &&
				body.stats.size() > 0 &&
				body.stats.get(0) != null &&
				body.stats.get(0).toString().contains("super")) {

			ListBuffer<JCTree.JCStatement> bodyNew = new ListBuffer<>();
			bodyNew.append(body.stats.get(0));
			bodyNew.append(callInfoMethodCall);

			for (int i = 1; i < body.stats.size(); i++) {
				bodyNew.append(body.stats.get(i));
			}

			body.stats = bodyNew.toList();

		} else {
			body.stats = body.stats.prepend(callInfoMethodCall);
		}

	}

	private String paramSuffix(String messagePattern) {
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(messagePattern);
		if (m.find()) {
			return m.group();
		}
		return "";
	}

}
