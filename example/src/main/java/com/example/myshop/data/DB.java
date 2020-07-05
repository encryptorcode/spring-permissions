package com.example.myshop.data;

import java.util.HashMap;
import java.util.Map;

public class DB<E extends Entity> {

    private final Map<Long, Entity> DATA = new HashMap<>();

    public void insert(E entity) {
        DATA.put(entity.getId(), entity);
    }

    public void update(E entity) {
        DATA.put(entity.getId(), entity);
    }

    public void delete(Long id) {
        DATA.remove(id);
    }

    public E get(Long id) {
        //noinspection unchecked
        return (E) DATA.get(id).clone();
    }

}
