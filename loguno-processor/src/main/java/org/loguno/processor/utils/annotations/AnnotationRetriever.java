package org.loguno.processor.utils.annotations;

import com.sun.source.tree.AnnotationTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.tools.javac.tree.JCTree;
import java.util.List;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public interface AnnotationRetriever {
    Stream<? extends Annotation> getTreeAnnotations(ModifiersTree modifiers);
    Stream<? extends Annotation> getTreeAnnotations(List<? extends AnnotationTree> annotations);
}
