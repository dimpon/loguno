package org.loguno.processor.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.loguno.processor.handlers.AnnotationHandler;
import org.loguno.processor.handlers.Handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
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

		///////////////////
		// ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();

		// ClassLoader classLoader = ScanPackageUtils.class.getClassLoader();
		final String path = packageName.replace('.', '/');

		Enumeration<URL> resources = CLASSLOADER.getResources(path);

		Collection<URL> urls = forPackage(HANDLERS_PACKAGE);
		for (Iterator<URL> iterator = urls.iterator(); iterator.hasNext();) {
			URL next = iterator.next();
			System.out.println("" + next);

			JarFile file = getJar(next);
			for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
				JarEntry jarEntry = entry.nextElement();
				if(!jarEntry.isDirectory()
						&&jarEntry.getName().endsWith(".class")
				&&  jarEntry.getName().startsWith(path)){

					String name = jarEntry.getName().replace("/", ".");
					Class<?> aClass = getClass(name);

					System.out.println("aClass:" + aClass);
				}



			}
		}

		////////////////////////////////

		Stream.Builder<File> builder = Stream.builder();

		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			if (resource.getProtocol().equals("jar")) {

				if (CLASSLOADER instanceof URLClassLoader) {

					URL[] urLs = ((URLClassLoader) CLASSLOADER).getURLs();

					Optional<URL> any = Arrays.stream(urLs).filter(url -> resource.getFile().contains(url.getFile())).findAny();

					String jarPath = "/D:/repo/org/loguno/loguno-processor/0.0.3-SNAPSHOT/loguno-processor-0.0.3-SNAPSHOT.jar";

					JarFile file = new JarFile(any.get().getFile());
					for (Enumeration<JarEntry> entry = file.entries(); entry.hasMoreElements();) {
						JarEntry jarEntry = entry.nextElement();
						if (!jarEntry.isDirectory()
								&& jarEntry.getName().endsWith(".class") && jarEntry.getName().startsWith(path)
						&& jarEntry.getName().startsWith(path)) {
							String name = jarEntry.getName().replace("/", ".");

							//Class<?> aClass = getClass(name);
							System.out.println(name);
						}
						// if(name.startsWith(packageName) && name.endsWith(".class"))
						// classes.add(Class.forName(name.substring(0, name.length() - 6)));
					}
					file.close();

				}
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
			return Stream.empty();// Collections.emptyList();
		}

		if (directory.listFiles() == null) {
			return Stream.empty();// Collections.emptyList();
		}

		return Arrays.<File> stream(directory.listFiles())
				.filter(file -> !file.isDirectory())
				.filter(file -> file.getName().endsWith(".class"))
				.map(file -> getClass(file, packageName));// .collect(Collectors.toList());

	}

	@SneakyThrows
	private Class<?> getClass(final File file, final String packageName) {
		return Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
	}

	@SneakyThrows
	private Class<?> getClass(final String classFullName) {
		return Class.forName(classFullName.substring(0, classFullName.length() - 6));
	}

	public static void main(String[] args) {

		List<? extends Class<? extends AnnotationHandler>> collect = ScanPackageUtils.getHandlersClasses()
				.filter(c -> !Modifier.isAbstract(c.getModifiers()))
				.filter(c -> c.isAnnotationPresent(Handler.class))
				.map(c -> (Class<? extends AnnotationHandler>) c)
				.collect(Collectors.toList());

	}

	@SneakyThrows
	private JarFile getJar(URL url) {

		URLConnection urlConnection = url.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			return ((JarURLConnection) urlConnection).getJarFile();
		}
        throw new RuntimeException("No jar for "+url);
	}

	private static Collection<URL> forPackage(String name) {
		return forResource(resourceName(name));
	}

	@SneakyThrows
	private static Collection<URL> forResource(String resourceName) {
		final List<URL> result = new ArrayList<>();
		// final ClassLoader[] loaders = classLoaders(classLoaders);
		// for (ClassLoader classLoader : loaders) {

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

		// }
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
		Map<String, URL> distinct = new LinkedHashMap<String, URL>(urls.size());
		for (URL url : urls) {
			distinct.put(url.toExternalForm(), url);
		}
		return distinct.values();
	}

}
