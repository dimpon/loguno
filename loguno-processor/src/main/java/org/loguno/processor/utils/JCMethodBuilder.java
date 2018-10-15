package org.loguno.processor.utils;

import com.sun.tools.javac.model.JavacElements;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Names;
import lombok.Builder;

@Builder
public class JCMethodBuilder {

    private final TreeMaker factory;

    private final Names names;

    private final JavacElements elements;

}
