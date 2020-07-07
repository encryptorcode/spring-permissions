package io.github.encryptorcode.permissions.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used for retrieving pipped variables from your {@link io.github.encryptorcode.permissions.abstracts.PermissionHandler} implementation
 * in your controller implementation by using this annotation for an argument for your controller.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerVariable {

    /**
     * Name of the variable you would like to retrieve
     *
     * @return String
     */
    String value();
}
