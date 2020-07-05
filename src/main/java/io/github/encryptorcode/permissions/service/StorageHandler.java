package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.Getter;

import java.util.HashMap;
import java.util.Map;

public class StorageHandler {
    private final Map<String, Object> storage = new HashMap<>();

    public <T> T get(String key, Getter<T> getter) {
        T object = getter.get();
        storage.put(key, object);
        return object;
    }

    public <T> T getExisting(String key, Getter<T> getter) {
        if (storage.containsKey(key)) {
            //noinspection unchecked
            return (T) storage.get(key);
        } else {
            return get(key, getter);
        }
    }

    Object get(String key){
        return storage.get(key);
    }
}
