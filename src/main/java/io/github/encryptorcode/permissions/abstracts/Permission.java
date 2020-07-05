package io.github.encryptorcode.permissions.abstracts;

import java.lang.annotation.*;

@Repeatable(Permissions.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.TYPE})
public @interface Permission {

    String name();

    String[] args() default {};
}
