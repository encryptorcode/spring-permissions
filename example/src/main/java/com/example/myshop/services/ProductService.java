package com.example.myshop.services;

import com.example.myshop.data.DB;
import com.example.myshop.data.ProductsDB;
import com.example.myshop.entities.Product;

public class ProductService {

    private final DB<Product> productDB = ProductsDB.get();

    public Product getProduct(Long id) {
        return productDB.get(id);
    }

    public void createProduct(Product product) {
        productDB.insert(product);
    }

    public void updateProduct(Long id, Product product) {
        product.setId(id);
        productDB.update(product);
    }

    public void deleteProduct(Long productId) {
        productDB.delete(productId);
    }
}
