package com.example.myshop.data;

import com.example.myshop.entities.Product;

public class ProductsDB {
    public static DB<Product> get() {
        return productDB;
    }

    private static final DB<Product> productDB = new DB<>();

    static {
        productDB.insert(new Product(1L, "Laptops", 250.00D, Product.Status.ACTIVE));
        productDB.insert(new Product(2L, "PC", 300.00D, Product.Status.INACTIVE));
        productDB.insert(new Product(3L, "Desktop", 100.00D, Product.Status.ACTIVE));
        productDB.insert(new Product(4L, "Monitor", 65.00D, Product.Status.INACTIVE));
        productDB.insert(new Product(5L, "Gaming Laptops", 450.00D, Product.Status.ACTIVE));
    }
}
