package com.example.myshop.entities;

import com.example.myshop.data.Entity;

public class Product extends Entity {
    private Long id;
    private String name;
    private Double price;
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    public Product() {
    }

    public Product(Long id, String name, Double price, Status status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
