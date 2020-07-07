package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.annotations.Permission;
import io.github.encryptorcode.permissions.annotations.Permissions;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This interceptor helps to validate permissions before actually invoking the controller method
 */
public class PermissionInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            List<Permission> permissions = new ArrayList<>();

            Permission permissionMethodAnnotation = handlerMethod.getMethodAnnotation(Permission.class);
            if (permissionMethodAnnotation != null) {
                permissions.add(permissionMethodAnnotation);
            }

            Permissions permissionsMethodAnnotation = handlerMethod.getMethodAnnotation(Permissions.class);
            if (permissionsMethodAnnotation != null) {
                permissions.addAll(Arrays.asList(permissionsMethodAnnotation.value()));
            }

            Class<?> controllerClass = handlerMethod.getBeanType();
            Permission permissionClassAnnotation = controllerClass.getAnnotation(Permission.class);
            if (permissionClassAnnotation != null) {
                permissions.add(permissionClassAnnotation);
            }

            Permissions permissionsClassAnnotation = handlerMethod.getMethodAnnotation(Permissions.class);
            if (permissionsClassAnnotation != null) {
                permissions.addAll(Arrays.asList(permissionsClassAnnotation.value()));
            }
            if (permissions.isEmpty()) {
                return true;
            } else {
                return PermissionManager.handle(request, response, permissions);
            }
        }
        return true;
    }

}
