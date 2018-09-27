package org.loguno.processor;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import org.loguno.processor.handlers.ClassContext;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"org.loguno.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class LogunoProcessor extends AbstractProcessor {

    /*private Trees trees;
    private TreeMaker treeMaker;
    private JavacTypes typeUtils;
    private JavacElements elementUtils;*/

    //LogunoScanner scanner = new LogunoScanner();
    private LogunoElementVisitor visitor;
    //LogunoTranslator translator = new LogunoTranslator();
    private JavacProcessingEnvironment javacProcessingEnvironment;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.javacProcessingEnvironment = (JavacProcessingEnvironment) processingEnv;
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

        if (annotations.isEmpty()) {
            return false;
        }

        Set<? extends Element> elements = roundEnvironment.getRootElements();

        elements.forEach(element -> {
            //messager.printMessage(Diagnostic.Kind.NOTE, "Simple Name: "+element);
            //System.out.println("Simple Name: " + element);

            ClassContext recorder = new ClassContext();
            System.out.println("recorder forward:" + recorder);
            ClassContext accept = element.accept(visitor, recorder);

            System.out.println("recorder back:" + accept);
            // JCTree tree = (JCTree) trees.getTree(element);
            //tree.accept(scanner);
            //tree.accept(translator);
        });

        //processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());//crash everything
        /*if (!roundEnvironment.processingOver()) {
            PlainClassWriter writer = new PlainClassWriter(processingEnv);
            writer.writeClass();
        }*/

        return true;
    }
}


