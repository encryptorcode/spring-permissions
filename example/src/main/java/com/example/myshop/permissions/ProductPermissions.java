package com.example.myshop.permissions;

import com.example.myshop.common.App;
import com.example.myshop.config.ResponseException;
import com.example.myshop.data.DB;
import com.example.myshop.data.ProductsDB;
import com.example.myshop.entities.Product;
import com.example.myshop.entities.User;
import io.github.encryptorcode.permissions.abstracts.PermissionHandler;
import io.github.encryptorcode.permissions.annotations.Handler;
import io.github.encryptorcode.permissions.service.Variables;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ProductPermissions extends PermissionHandler {

    public ProductPermissions(Variables variables) {
        super(variables);
    }

    private final DB<Product> productDB = ProductsDB.get();

    @Handler(id = "product.view")
    public void productView(Long id) throws ResponseException {
        User user = getCurrentUser();
        Product product = variables.pipe("product", () -> productDB.get(id));
        boolean productActive = product.getStatus() == Product.Status.ACTIVE;
        boolean adminUser = user != null && user.getRole() == User.Role.ADMIN;
        if (!productActive && !adminUser) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Invalid Id provided.");
        }
    }

    @Handler(id = "product.edit")
    public void productEdit(Long id) throws ResponseException {
        User user = getCurrentUser();
        if (user == null) {
            throw new ResponseException(HttpStatus.FORBIDDEN, "You need to login to edit a product.");
        }
        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseException(HttpStatus.FORBIDDEN, "Only admin can edit a product.");
        }

        Product product = variables.pipe("product", () -> productDB.get(id));
        if (product == null) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Invalid product id specified.");
        }
    }

    @Handler(id = "product.create")
    public void productCreate() throws ResponseException {
        User user = getCurrentUser();
        if (user == null) {
            throw new ResponseException(HttpStatus.FORBIDDEN, "You need to login to edit a product.");
        }
        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseException(HttpStatus.FORBIDDEN, "Only admin can create a product.");
        }
    }

    private User getCurrentUser() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return (User) request.getAttribute(App.USER_ATTRIBUTE);
        }
        return null;
    }
}
