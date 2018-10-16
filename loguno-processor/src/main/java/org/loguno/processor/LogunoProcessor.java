package org.loguno.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.util.Options;
import org.loguno.Loguno;
import org.loguno.processor.configuration.Configuration;
import org.loguno.processor.configuration.ConfigurationImpl;
import org.loguno.processor.configuration.ConfigurationKeys;
import org.loguno.processor.configuration.ConfiguratorManager;
import org.loguno.processor.handlers.ClassContext;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes({"org.loguno.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class LogunoProcessor extends AbstractProcessor {

    //LogunoScanner scanner = new LogunoScanner();
    private LogunoElementVisitor visitor;
    //LogunoTranslator translator = new LogunoTranslator();
    private JavacProcessingEnvironment javacProcessingEnvironment;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        this.javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnv;
        Options options = Options.instance(this.javacProcessingEnvironment.getContext());
        String sourcepath = options.get("-sourcepath");
        String userdir = System.getProperties().getProperty("user.dir");
        ConfigurationImpl.sourcepath = sourcepath;
        ConfigurationImpl.userdir = userdir;


        this.visitor = new LogunoElementVisitor(javacProcessingEnvironment);


        /*this.trees = Trees.instance(javacProcessingEnvironment);
        this.typeUtils = javacProcessingEnvironment.getTypeUtils();
        this.elementUtils = javacProcessingEnvironment.getElementUtils();
        this.treeMaker = TreeMaker.instance(javacProcessingEnvironment.getContext());
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "LogunoProcessor.Init..." + Thread.currentThread());
*/
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {

        long start = System.currentTimeMillis();

        Configuration conf = ConfiguratorManager.getInstance().getConfiguration();
        Boolean enable = conf.getProperty(ConfigurationKeys.ENABLE);

        if(!enable)
            return true;

        if (annotations.isEmpty()) {
            return false;
        }


        Set<? extends Element> elementsAnnotatedWith1 = roundEnvironment.getElementsAnnotatedWith(Loguno.class);
        Set<TypeElement> elementsAnnotatedWith = (Set<TypeElement>)roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class);
        List<NestingKind> collect = elementsAnnotatedWith.stream().map(typeElement -> typeElement.getNestingKind()).collect(Collectors.toList());


        Set<TypeElement> elements = roundEnvironment.getElementsAnnotatedWith(Loguno.Logger.class).stream()
                .map(o -> (TypeElement)o)
                .filter(o -> o.getNestingKind()==NestingKind.TOP_LEVEL)
                .collect(Collectors.toSet());

        //todo send only annotated classes. it saves performance.
        //Set<? extends Element> elements = roundEnvironment.getRootElements();


        try {
            final ClassContext recorder = new ClassContext();
            elements.forEach(element -> {
                //messager.printMessage(Diagnostic.Kind.NOTE, "Simple Name: "+element);
                //System.out.println("Simple Name: " + element);


                Void accept = element.accept(visitor, recorder);

                // JCTree tree = (JCTree) trees.getTree(element);
                //tree.accept(scanner);
                //tree.accept(translator);
                System.out.println("recorder="+recorder);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());//crash everything
        /*if (!roundEnvironment.processingOver()) {
            PlainClassWriter writer = new PlainClassWriter(processingEnv);
            writer.writeClass();
        }*/

        long end = System.currentTimeMillis();
        System.out.println("exec time, Millis:"+(end-start));

        return true;
    }
}


