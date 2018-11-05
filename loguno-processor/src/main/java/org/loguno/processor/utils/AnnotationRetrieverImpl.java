package org.loguno.processor.utils;

import com.sun.tools.javac.tree.JCTree;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public class AnnotationRetrieverImpl implements AnnotationRetriever {
    @Override
    public Stream<? extends Annotation> getTreeAnnotations(JCTree tree) {
        return null;
    }

    private void prim(){}

}
