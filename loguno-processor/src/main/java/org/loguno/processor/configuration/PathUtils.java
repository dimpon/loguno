package org.loguno.processor.configuration;

import com.sun.tools.javac.code.Symbol;
import lombok.experimental.UtilityClass;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@UtilityClass
public class PathUtils {

    public String getFilePath(Element file) {
        if(file instanceof TypeElement) {
            JavaFileObject sourcefile = ((Symbol.ClassSymbol) file).sourcefile;
            return sourcefile.getName();
        }
        if(file instanceof PackageElement){
            return ((Symbol.PackageSymbol) file).fullname.toString();
        }
        throw new IllegalArgumentException(file.toString());
    }

    public String getPropertiesPotentialPath(Element file) {
        String name = getFilePath(file);
        String qName = ((Symbol.ClassSymbol) file).getQualifiedName().toString();
        String stopName =  qName.replace(".", "\\");
        return name.substring(0, name.lastIndexOf(stopName));
    }
}
