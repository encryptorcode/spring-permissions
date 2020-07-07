package io.github.encryptorcode.permissions.entities;

import java.lang.reflect.Method;

public class HandlerData {
    private final String id;
    private final Method method;

    public HandlerData(String id, Method method) {
        this.id = id;
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public Method getMethod() {
        return method;
    }
}
