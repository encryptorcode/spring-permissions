package com.example.myshop.controllers;

import com.example.myshop.config.JsonRequestBody;
import com.example.myshop.entities.Product;
import com.example.myshop.services.ProductService;
import io.github.encryptorcode.permissions.annotations.HandlerVariable;
import io.github.encryptorcode.permissions.annotations.Permission;
import io.github.encryptorcode.permissions.annotations.Permissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/v1/products")
public class ProductsController {

    ProductService service = new ProductService();

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    @Permission(id = "product.view", args = {"${path.id}"})
    @ResponseBody
    public Product getProduct(
            @HandlerVariable("product") Product product
    ) {
        return product;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    @Permission(id = "product.create")
    @ResponseBody
    public void createProduct(
            @JsonRequestBody Product product
    ) {
        service.createProduct(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @Permission(id = "product.edit", args = {"${path.id}"})
    @ResponseBody
    public void updateProduct(
            @PathVariable("id") Long id,
            @JsonRequestBody Product product
    ) {
        service.updateProduct(id, product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @Permission(id = "product.edit", args = {"${path.id}"})
    @ResponseBody
    public void deleteProduct(
            @PathVariable("id") Long id
    ) {
        service.deleteProduct(id);
    }
}
