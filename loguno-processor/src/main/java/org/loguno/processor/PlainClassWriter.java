package org.loguno.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public class PlainClassWriter {

    private final ProcessingEnvironment processingEnv;

    public PlainClassWriter(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    void writeClass() {

        try {
            JavaFileObject builderFile = processingEnv.getFiler()
                    .createSourceFile("com.test.PetsOwner");
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
                out.print("package com.test;\n");
                out.print("@lombok.Getter\n");
                out.print("public class PetsOwner {\n");
                out.print("private String namex;\n");
                out.print("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, e.getMessage());
        }
    }
}
