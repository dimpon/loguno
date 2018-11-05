package org.loguno.processor.utils;

import com.sun.source.tree.ModifiersTree;
import com.sun.tools.javac.tree.JCTree;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public interface AnnotationRetriever {
    Stream<? extends Annotation> getTreeAnnotations(ModifiersTree modifiers);
}
