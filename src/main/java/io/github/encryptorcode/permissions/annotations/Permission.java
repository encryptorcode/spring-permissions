package io.github.encryptorcode.permissions.annotations;

import java.lang.annotation.*;

/**
 * This annotation should be used on your controller methods to validate for permissions before your controller invocation is actually invoked.
 * You can specify multiple annotations to validate multiple permissions.
 */
@Repeatable(Permissions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Permission {

    /**
     * Permission id to be validated
     *
     * @return String
     */
    String id();

    /**
     * Arguments if any, to be passed to your implementation of {@link io.github.encryptorcode.permissions.abstracts.PermissionHandler}
     *
     * @return String array
     */
    String[] args() default {};
}
