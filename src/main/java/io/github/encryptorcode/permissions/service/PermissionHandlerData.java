package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.PermissionHandler;

import java.lang.reflect.Method;

public class PermissionHandlerData {
    private final String id;
    private final Class<? extends PermissionHandler> clazz;
    private final Method method;

    public PermissionHandlerData(String id, Class<? extends PermissionHandler> clazz, Method method) {
        this.id = id;
        this.clazz = clazz;
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public Class<? extends PermissionHandler> getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }
}
