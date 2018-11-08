package org.loguno.processor.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.Handler;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.*;
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

    private ClassLoader CLASSLOADER = ScanPackageUtils.class.getClassLoader();

    @SneakyThrows
    private Stream<Class<?>> getClasses(String packageName) {

        final String path = packageName.replace('.', '/');

        Collection<URL> urls = forPackage(HANDLERS_PACKAGE);
        Stream.Builder<Class<?>> builder = Stream.builder();

        for (URL url : urls) {

            String protocol = url.getProtocol();

            if (protocol.endsWith("jar")) {
                JarFile file = getJar(url);
                for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements(); ) {
                    JarEntry jarEntry = entry.nextElement();
                    if (!jarEntry.isDirectory()
                            && jarEntry.getName().endsWith(".class")
                            && jarEntry.getName().startsWith(path)) {

                        String name = jarEntry.getName().replace("/", ".");
                        Class<?> aClass = getClass(name);
                        builder.add(aClass);
                    }
                }
            }


            if (protocol.endsWith("file")) {
                String path1 = url.toURI().getSchemeSpecificPart();
                java.io.File file = new java.io.File(path1 + path);
                if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (File f : files) {
                        if (!f.isDirectory()
                                && f.getName().endsWith(".class")) {
                            String name = f.getName().replace("/", ".");
                            Class<?> aClass = getClass(HANDLERS_PACKAGE + "." + name);
                            builder.add(aClass);
                        }
                    }
                }
            }
        }
        return builder.build();
    }

    @SneakyThrows
    private Class<?> getClass(final String classFullName) {
        return Class.forName(classFullName.substring(0, classFullName.length() - 6));
    }


    @SneakyThrows
    private JarFile getJar(URL url) {

        URLConnection urlConnection = url.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            return ((JarURLConnection) urlConnection).getJarFile();
        }
        throw new RuntimeException("That is not JarURLConnection " + url);
    }

    private static Collection<URL> forPackage(String name) {
        return forResource(resourceName(name));
    }

    @SneakyThrows
    private static Collection<URL> forResource(String resourceName) {
        final List<URL> result = new ArrayList<>();

        final Enumeration<URL> urls = CLASSLOADER.getResources(resourceName);
        while (urls.hasMoreElements()) {
            final URL url = urls.nextElement();
            int index = url.toExternalForm().lastIndexOf(resourceName);
            if (index != -1) {
                // Add old url as contextUrl to support exotic url handlers
                result.add(new URL(url, url.toExternalForm().substring(0, index)));
            } else {
                result.add(url);
            }
        }

        return distinctUrls(result);
    }

    private static String resourceName(String name) {
        if (name != null) {
            String resourceName = name.replace(".", "/");
            resourceName = resourceName.replace("\\", "/");
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            return resourceName;
        }
        return null;
    }

    private static Collection<URL> distinctUrls(Collection<URL> urls) {
        Map<String, URL> distinct = new LinkedHashMap<>(urls.size());
        for (URL url : urls) {
            distinct.put(url.toExternalForm(), url);
        }
        return distinct.values();
    }

}
