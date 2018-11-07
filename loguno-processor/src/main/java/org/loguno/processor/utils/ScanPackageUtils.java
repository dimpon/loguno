package org.loguno.processor.utils;


import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.Handler;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ScanPackageUtils {

    private final String HANDLERS_PACKAGE = "org.loguno.processor.handlers";

    public Stream<Class<?>> getHandlersClasses() {
        return getClasses(HANDLERS_PACKAGE);
    }

    @SneakyThrows
    private Stream<Class<?>> getClasses(String packageName) {



        ///////////////////
        //ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();

        ClassLoader classLoader = ScanPackageUtils.class.getClassLoader();
        final String path = packageName.replace('.', '/');

        Enumeration<URL> resources = classLoader.getResources(path);


        Stream.Builder<File> builder = Stream.builder();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if(resource.getProtocol().equals("jar")){

                String jarPath = "/D:/repo/org/loguno/loguno-processor/0.0.3-SNAPSHOT/loguno-processor-0.0.3-SNAPSHOT.jar";

                JarFile file = new JarFile(jarPath);
                for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
                    JarEntry jarEntry = entry.nextElement();
                    String name = jarEntry.getName().replace("/", ".");
                    //if(name.startsWith(packageName) && name.endsWith(".class"))
                    //    classes.add(Class.forName(name.substring(0, name.length() - 6)));
                }
                file.close();


            }

            builder.add(new File(resource.getFile()));
        }

        return builder.build()
                .map(dir -> findClasses(dir, packageName))
                .flatMap(Function.identity());
    }


    @SneakyThrows
    private Stream<Class<?>> findClasses(final File directory, final String packageName) {

        if (!directory.exists()) {
            return Stream.empty();//Collections.emptyList();
        }

        if (directory.listFiles() == null) {
            return Stream.empty();//Collections.emptyList();
        }

        return Arrays.<File>stream(directory.listFiles())
                .filter(file -> !file.isDirectory())
                .filter(file -> file.getName().endsWith(".class"))
                .map(file -> getClass(file, packageName));//.collect(Collectors.toList());

    }

    @SneakyThrows
    private Class<?> getClass(final File file, final String packageName) {
        return Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
    }

    public static void main(String[] args) {


        List<? extends Class<? extends AnnotationHandler>> collect = ScanPackageUtils.getHandlersClasses()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .filter(c -> c.isAnnotationPresent(Handler.class))
                .map(c -> (Class<? extends AnnotationHandler>) c)
                .collect(Collectors.toList());

    }
}
