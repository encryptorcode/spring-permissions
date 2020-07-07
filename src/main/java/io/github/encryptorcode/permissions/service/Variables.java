package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.Pipeline;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to pipe variables generated from the {@link io.github.encryptorcode.permissions.abstracts.PermissionHandler} layer to the controller layer on demand.
 */
public class Variables {

    protected Variables() {
    }

    private final Map<String, Object> storage = new HashMap<>();

    /**
     * Used to pipe your variables from your {@link io.github.encryptorcode.permissions.abstracts.PermissionHandler} implementation. <br>
     * <b>Note:</b> This method will return the variable that is already set if there are any set in the previous permission checks.
     * To skip checking the variable and always call the lambda implementation use {@link #pipeNew(String, Pipeline)} instead
     *
     * @param key      Key that can be used to retrieve a variable in controller using {@link io.github.encryptorcode.permissions.annotations.HandlerVariable @HandlerVariable} annotation
     * @param pipeline Use a lambda implementation to write your business logic to retrieve data
     * @param <T>      variable type
     * @return variable retrieved from pipeline
     */
    public <T> T pipe(String key, Pipeline<T> pipeline) {
        if (storage.containsKey(key)) {
            //noinspection unchecked
            return (T) storage.get(key);
        } else {
            return pipeNew(key, pipeline);
        }
    }

    /**
     * This method is similar to {@link #pipe(String, Pipeline)} method, But in this method instead of checking if the
     * variable is already existing it would always get the new variable
     *
     * @param key      Key that can be used to retrieve a variable in controller using {@link io.github.encryptorcode.permissions.annotations.HandlerVariable @HandlerVariable} annotation
     * @param pipeline Use a lambda implementation to write your business logic to retrieve data
     * @param <T>      variable type
     * @return variable retrieved from pipeline
     */
    public <T> T pipeNew(String key, Pipeline<T> pipeline) {
        T object = pipeline.pipe();
        storage.put(key, object);
        return object;
    }

    /**
     * Internal get implementation used to get variable based on provided key.
     *
     * @param key key
     * @return value of the variable if any or null
     */
    Object get(String key) {
        return storage.get(key);
    }
}
