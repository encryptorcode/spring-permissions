package com.example.myshop.controllers;

import com.example.myshop.config.JsonRequestBody;
import com.example.myshop.entities.Product;
import com.example.myshop.services.ProductService;
import io.github.encryptorcode.permissions.abstracts.Permission;
import io.github.encryptorcode.permissions.abstracts.PermissionVariable;
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
    @Permission(name = "product.view", args = "${path.id}")
    @ResponseBody
    public Product getProduct(
            @PermissionVariable("product") Product product
    ) {
        return product;
    }

    @RequestMapping(path = "", method = RequestMethod.POST)
    @ResponseBody
    public void createProduct(
            @JsonRequestBody Product product
    ) {
        service.createProduct(product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    @Permission(name = "product.edit", args = {"${path.id}"})
    @ResponseBody
    public void updateProduct(
            @PathVariable("id") Long id,
            @JsonRequestBody Product product
    ) {
        service.updateProduct(id, product);
    }

    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    @Permission(name = "product.edit", args = {"${path.id}"})
    @ResponseBody
    public void deleteProduct(
            @PathVariable("id") Long id
    ) {
        service.deleteProduct(id);
    }
}
