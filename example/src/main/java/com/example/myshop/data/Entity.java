package com.example.myshop.data;

public abstract class Entity implements Cloneable {
    public abstract Long getId();

    public Entity clone(){
        try {
            return (Entity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
