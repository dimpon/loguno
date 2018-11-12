package org.loguno.processor.configuration;

public class ThreadLocalHolder {
    private static final ThreadLocal<String> THREAD = new ThreadLocal<>();

    private ThreadLocalHolder() {
    }

    public static String get() {
        return THREAD.get();
    }

    public static void put(String file) {
        THREAD.set(file);
    }

    public static void cleanupThread() {
        THREAD.remove();
    }
}
