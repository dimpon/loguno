package org.loguno.processor;

import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.Loguno;
import org.loguno.processor.configuration.*;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.handlers.InstrumentsHolder;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({ "org.loguno.Loguno.Logger" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LogunoProcessor extends AbstractProcessor {


	private JavacProcessingEnvironment javacProcessingEnvironment;
	private InstrumentsHolder holder;

	private Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnv;
		this.holder = new InstrumentsHolder(javacProcessingEnvironment);
		HandlersProvider.create(this.javacProcessingEnvironment);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

		long start = System.currentTimeMillis();
		final ClassContext classContext = new ClassContext();
		try {
			if (annotations.isEmpty() || roundEnvironment.getRootElements().isEmpty()) {
				return false;
			}

			Element fElement = roundEnvironment.getRootElements().stream().findFirst().orElseThrow(() -> new RuntimeException("No elements to compile."));
			String rootPath = PathUtils.getPropertiesPotentialPath(fElement);
			Boolean enable = conf.getProperty(ConfigurationKeys.ENABLE, rootPath);

			if (!enable)
				return false;

			Set<Element> elements = roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class).stream()
					// .map(o -> (Element) o)
					.filter(this::isProcess)
					//.filter(typeElement -> typeElement.getSimpleName().toString().contains("UseStandardConfig"))
					.collect(Collectors.toSet());


			final LogunoScanner scanner = new LogunoScanner(classContext, holder.retriever);

			elements.forEach(element -> {

				ThreadLocalHolder.put(PathUtils.getFilePath(element));
				Boolean isProcessElement = conf.getProperty(ConfigurationKeys.ENABLE);
				if (isProcessElement) {
					JCTree tree = javacProcessingEnvironment.getElementUtils().getTree(element);
					scanner.scan(tree);
				}

				ThreadLocalHolder.cleanupThread();

			});
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(classContext.toString());
			javacProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage()+classContext);
		}


		long end = System.currentTimeMillis();
		System.out.println("exec time, Millis:" + (end - start));

		return true;
	}

	private boolean isProcess(Element e) {
		if (e.getKind() == ElementKind.PACKAGE)
			return true;

		if (e.getKind() == ElementKind.CLASS) {
			return !((TypeElement) e).getNestingKind().isNested();
		}

		return false;

	}
}
