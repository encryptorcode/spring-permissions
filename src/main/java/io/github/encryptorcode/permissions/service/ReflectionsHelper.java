package io.github.encryptorcode.permissions.service;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Reflections helper to handle all the use-cases that require reflections
 */
public class ReflectionsHelper {

    private static Reflections reflections;

    /**
     * This init will be invoked from the init method of {@link io.github.encryptorcode.permissions.service.PermissionManager}
     *
     * @param packageNames names of packages to be scanned.
     */
    static void init(String... packageNames) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .forPackages(packageNames)
                .setScanners(new MethodAnnotationsScanner(), new SubTypesScanner());
        reflections = new Reflections(configurationBuilder);
    }

    /**
     * Gets all methods annotated with the given annotation class
     *
     * @param annotationClazz Annotation class
     * @return Unique list of methods annotated with the given class
     */
    public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationClazz) {
        return reflections.getMethodsAnnotatedWith(annotationClazz);
    }

    /**
     * Gets all the sub-classes for the given class
     * @param clazz given class
     * @param <T> class type
     * @return Unique list of classes with the given super-class
     */
    public static <T> Set<Class<? extends T>> getSubClassesOf(Class<T> clazz) {
        return reflections.getSubTypesOf(clazz);
    }
}
