package org.loguno.processor.handlers;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import lombok.SneakyThrows;

import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class scans package with annotation handlers, instanciates them, saves in {@link #handlers}
 * Returns the list of supported annotations, returns handlers by distinct Type and annotation.
 */
public final class HandlersProvider {

	private static final String HANDLERS_PACKAGE = "org.loguno.processor.handlers";

	private final Map<Class<?>, Map<Class<? extends Annotation>, List<AnnotationHandler>>> handlers;

	private final Set<Class<? extends Annotation>> supportedAnnotations;

	private static HandlersProvider INSTANCE;

	public static HandlersProvider create(JavacProcessingEnvironment environment) {
		INSTANCE = new HandlersProvider(environment);
		return INSTANCE;
	}

    public static HandlersProvider instance() {
	    return INSTANCE;
    }

	private HandlersProvider(JavacProcessingEnvironment environment) {

		List<Class<? extends AnnotationHandler>> allHandlersClasses = getAnnotationHandlersClasses()
				.collect(Collectors.toList());

		this.handlers = allHandlersClasses.stream()
				.map(c -> createHandler(c, environment))
				.collect(Collectors.groupingBy(AnnotationHandler::getElementClass,
						Collectors.groupingBy(AnnotationHandler::getAnnotationClass)));

		this.supportedAnnotations = this.handlers.entrySet().stream()
				.map(Map.Entry::getValue)
				.flatMap(m -> m.keySet().stream())
				.collect(Collectors.toSet());
	}

	@SuppressWarnings("unchecked")
	public <E> Stream<? extends AnnotationHandler<?, E>> getHandlersByElementAndAnnotation(Class<? extends Annotation> a, E e) {
		return handlers
				.getOrDefault(keyClass(e), Collections.emptyMap())
				.getOrDefault(a, Collections.emptyList())
				.stream()
				.sorted(Comparator.comparing(h -> h.getClass().getAnnotation(Order.class).value()))
				.map(h -> (AnnotationHandler<?, E>) h);
	}

	public Map<Class<? extends Annotation>, List<AnnotationHandler>> getHandlersaByElement(Object e) {
		return handlers.getOrDefault(keyClass(e), Collections.emptyMap());
	}

	public Set<Class<? extends Annotation>> supportedAnnotations() {
		return this.supportedAnnotations;
	}

	public Optional<Class<? extends Annotation>> getAnnotationClassByName(final String name) {
		return this.supportedAnnotations.stream().filter(c -> c.getName().endsWith(name)).findAny();
	}

	@SneakyThrows({ InstantiationException.class, IllegalAccessException.class, NoSuchMethodException.class, InvocationTargetException.class })
	private AnnotationHandler createHandler(Class<? extends AnnotationHandler> clazz, JavacProcessingEnvironment environment) {
		return clazz.getConstructor(JavacProcessingEnvironment.class).newInstance(environment);
	}

	// todo if performance is slow try https://github.com/atteo/classindex https://github.com/classgraph/classgraph
	private Stream<Class<? extends AnnotationHandler>> getAnnotationHandlersClasses() {

		Stream<Class<? extends AnnotationHandler>> ha = Stream.<Class<? extends AnnotationHandler>>builder()
				.add(AnnotationHandlerLogger.class)
				.add(AnnotationHandlerLoggerLazy.class)

				.add(AnnotationHandlerMethod.AnnotationHandlerLoguno.class)
				.add(AnnotationHandlerMethod.AnnotationHandlerInfo.class)
				.add(AnnotationHandlerMethod.AnnotationHandlerWarn.class)
				.add(AnnotationHandlerMethod.AnnotationHandlerDebug.class)
				.add(AnnotationHandlerMethod.AnnotationHandlerTrace.class)
				.add(AnnotationHandlerMethod.AnnotationHandlerError.class)

				.add(AnnotationHandlerMethodParams.AnnotationHandlerLoguno.class)
				.add(AnnotationHandlerMethodParams.AnnotationHandlerInfo.class)
				.add(AnnotationHandlerMethodParams.AnnotationHandlerWarn.class)
				.add(AnnotationHandlerMethodParams.AnnotationHandlerDebug.class)
				.add(AnnotationHandlerMethodParams.AnnotationHandlerTrace.class)
				.add(AnnotationHandlerMethodParams.AnnotationHandlerError.class)

				.add(AnnotationHandlerLocalVariable.AnnotationHandlerLoguno.class)
				.add(AnnotationHandlerLocalVariable.AnnotationHandlerInfo.class)
				.add(AnnotationHandlerLocalVariable.AnnotationHandlerWarn.class)
				.add(AnnotationHandlerLocalVariable.AnnotationHandlerDebug.class)
				.add(AnnotationHandlerLocalVariable.AnnotationHandlerTrace.class)
				.add(AnnotationHandlerLocalVariable.AnnotationHandlerError.class)

				.add(AnnotationHandlerCatch.AnnotationHandlerLoguno.class)
				.add(AnnotationHandlerCatch.AnnotationHandlerInfo.class)
				.add(AnnotationHandlerCatch.AnnotationHandlerWarn.class)
				.add(AnnotationHandlerCatch.AnnotationHandlerDebug.class)
				.add(AnnotationHandlerCatch.AnnotationHandlerTrace.class)
				.add(AnnotationHandlerCatch.AnnotationHandlerError.class)

				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerPipedExceptions.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerLoguno.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerInfo.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerWarn.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerDebug.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerTrace.class)
				.add(AnnotationHandlerPipedExceptionsCatch.AnnotationHandlerError.class)

				.add(AnnotationHandlerMethodThrows.AnnotationHandlerWholeMethod.class)

		.build();


		return ha.filter(c -> !Modifier.isAbstract(c.getModifiers()))
				.filter(c -> c.isAnnotationPresent(Handler.class));
	}

	/**
	 * Finds the interface class which shows the real type of element.
	 * The class is used as key for getting handlers from {@link HandlersProvider#handlers}
	 *
	 * @param e
	 *            Element of class
	 * @return class if interface which is subtype of {@link Element}
	 */
	@SuppressWarnings("unchecked")
	private Class<?> keyClass(Object e) {
		Class<?> clazz = e.getClass();
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> i : interfaces) {
			if (isClassSubtypeOfElement(i))
				return i;
		}
		return e.getClass();
	}

	private static boolean isClassSubtypeOfElement(Class<?> clazz) {
		Set<Class<?>> asSet = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
		return asSet.contains(Element.class);
	}
}
