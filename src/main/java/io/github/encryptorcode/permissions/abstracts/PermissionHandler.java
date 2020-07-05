package io.github.encryptorcode.permissions.abstracts;

import io.github.encryptorcode.permissions.service.StorageHandler;

public abstract class PermissionHandler {

    protected StorageHandler storageHandler;

    public PermissionHandler(StorageHandler storageHandler) {
        this.storageHandler = storageHandler;
    }
}
