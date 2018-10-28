package org.loguno.processor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Options;
import org.loguno.Loguno;
import org.loguno.processor.configuration.*;
import org.loguno.processor.handlers.ClassContext;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"org.loguno.Loguno.Logger"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class LogunoProcessor extends AbstractProcessor {

    //LogunoScanner scanner = new LogunoScanner();

    //LogunoTranslator translator = new LogunoTranslator();
    private JavacProcessingEnvironment javacProcessingEnvironment;

    private Configuration conf = ConfiguratorManager.getInstance().getConfiguration();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnv;


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

    private String getFilePath(Element file){
        JavaFileObject sourcefile = ((Symbol.ClassSymbol) file).sourcefile;
        return sourcefile.getName();
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



        /*Set<? extends Element> rootElements = roundEnvironment.getRootElements();

        rootElements.forEach(o -> {
            TypeElement e =((TypeElement) o);
            NestingKind nestingKind = e.getNestingKind();
            Loguno.Logger a = e.getAnnotation(Loguno.Logger.class);
        });

        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class);
*/


        Set<TypeElement> elements = roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class).stream()
                .map(o -> (TypeElement) o)
                .filter(o -> o.getNestingKind() == NestingKind.TOP_LEVEL)
                .collect(Collectors.toSet());

        final ClassContext classContext = new ClassContext();
        final LogunoElementVisitor visitor = new LogunoElementVisitor(javacProcessingEnvironment);
        try {
            elements.forEach(element -> {

                ThreadLocalHolder.put(getFilePath(element));

                Void accept = element.accept(visitor, classContext);

                ThreadLocalHolder.cleanupThread();
                // JCTree tree = (JCTree) trees.getTree(element);
                //tree.accept(scanner);
                //tree.accept(translator);
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
}


