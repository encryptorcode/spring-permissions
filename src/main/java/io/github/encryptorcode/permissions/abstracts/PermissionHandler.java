package io.github.encryptorcode.permissions.abstracts;

import io.github.encryptorcode.permissions.service.Variables;

/**
 * This class has to be extended to your handler class that contains methods that will be used for validation.
 */
public abstract class PermissionHandler {

    protected Variables variables;

    /**
     * Override this and have a constructor with {@link Variables} in your implementation too.
     *
     * @param variables an instance of variables will be passed to your permission handler to store variables
     *                  that can be retrieved later in controller using {@link io.github.encryptorcode.permissions.annotations.HandlerVariable @HandlerVariable} annotation
     */
    public PermissionHandler(Variables variables) {
        this.variables = variables;
    }
}
