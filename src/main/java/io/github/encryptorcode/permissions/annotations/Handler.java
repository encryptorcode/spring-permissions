package io.github.encryptorcode.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to map methods in {@link io.github.encryptorcode.permissions.abstracts.PermissionHandler} implementation
 * that are used for validating permissions
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handler {
    /**
     * Permission id to validate
     *
     * @return String
     */
    String id();
}
