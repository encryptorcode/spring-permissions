package io.github.encryptorcode.permissions.service;

import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ReflectionsHelper {
    private static final Reflections reflections = new Reflections("com.example");

    public static Set<Class<?>> getAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public static <T> Set<Class<? extends T>> getSubClassesOf(Class<T> clazz) {
        return reflections.getSubTypesOf(clazz);
    }
}
