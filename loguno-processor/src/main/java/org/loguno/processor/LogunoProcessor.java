package org.loguno.processor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import org.loguno.Loguno;
import org.loguno.processor.configuration.*;
import org.loguno.processor.handlers.ClassContext;
import org.loguno.processor.handlers.HandlersProvider;
import org.loguno.processor.handlers.InstrumentsHolder;
import org.loguno.processor.utils.annotations.AnnotationRetriever;
import org.loguno.processor.utils.annotations.AnnotationRetrieverImpl;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"org.loguno.Loguno.Logger"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LogunoProcessor extends AbstractProcessor {

    //LogunoScanner scanner = new LogunoScanner();

    //LogunoTranslator translator = new LogunoTranslator();
    private JavacProcessingEnvironment javacProcessingEnvironment;
    private InstrumentsHolder holder;

    private Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnv;
        this.holder = new InstrumentsHolder(javacProcessingEnvironment);
        HandlersProvider.create(this.javacProcessingEnvironment);




        /*Options options = Options.instance(this.javacProcessingEnvironment.getContext());
        String sourcepath = options.get("-sourcepath");
        String userdir = System.getProperties().getProperty("user.dir");
        ConfigurationImpl.sourcepath = sourcepath;
        ConfigurationImpl.userdir = userdir;
*/




        /*this.trees = Trees.create(javacProcessingEnvironment);
        this.typeUtils = javacProcessingEnvironment.getTypeUtils();
        this.elementUtils = javacProcessingEnvironment.getElementUtils();
        this.treeMaker = TreeMaker.create(javacProcessingEnvironment.getContext());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "LogunoProcessor.Init..." + Thread.currentThread());
*/
    }

    private String getFilePath(Element file) {
        if(file instanceof TypeElement) {
            JavaFileObject sourcefile = ((Symbol.ClassSymbol) file).sourcefile;
            return sourcefile.getName();
        }
        if(file instanceof PackageElement){
            return ((Symbol.PackageSymbol) file).fullname.toString();
        }
        throw new IllegalArgumentException(file.toString());
    }

    private String getPropertiesPotentialPath(Element file) {
        String name = getFilePath(file);
        return name.substring(0, name.lastIndexOf("src"));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        long start = System.currentTimeMillis();

        if (annotations.isEmpty() || roundEnvironment.getRootElements().isEmpty()) {
            return false;
        }

        Set<? extends Element> rootElements = roundEnvironment.getRootElements();

        Element firstFile = rootElements.stream().findFirst().orElseGet(() -> null);

        String rootPath = getPropertiesPotentialPath(firstFile);


        Boolean enable = conf.getProperty(ConfigurationKeys.ENABLE, rootPath);


        if (!enable)
            return false;



        Set<Element> elements = roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class).stream()
                //.map(o -> (Element) o)
                .filter(this::isProcess)
                .filter(typeElement -> typeElement.getSimpleName().toString().contains("Makaka"))

                .collect(Collectors.toSet());

        final ClassContext classContext = new ClassContext();
        final AnnotationRetriever retriever = new AnnotationRetrieverImpl();

        final LogunoScanner scanner = new LogunoScanner(classContext,retriever);

        try {
            elements.forEach(element -> {

                ThreadLocalHolder.put(getFilePath(element));
                JCTree tree = javacProcessingEnvironment.getElementUtils().getTree(element);
                scanner.scan(tree);

                ThreadLocalHolder.cleanupThread();

            });
        } catch (Exception e) {
            System.out.println(classContext.toString());
            e.printStackTrace();
            javacProcessingEnvironment.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }

        //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());//crash everything
        /*if (!roundEnvironment.processingOver()) {
            PlainClassWriter writer = new PlainClassWriter(processingEnv);
            writer.writeClass();
        }*/

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


