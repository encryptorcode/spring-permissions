package io.github.encryptorcode.permissions.abstracts;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionVariable {
    String value();
}
