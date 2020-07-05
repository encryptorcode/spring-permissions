package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.Permission;
import io.github.encryptorcode.permissions.abstracts.Permissions;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            if(permissions.size() > 0) {
                return invoke(request, response, permissions);
            } else {
                return true;
            }
        }
        return true;
    }

    private boolean invoke(HttpServletRequest request, HttpServletResponse response, List<Permission> permissions) throws Exception {
        StorageHandler storageHandler = new StorageHandler();
        for (Permission permission : permissions) {
            if (!PermissionsManager.invoke(request, permission, storageHandler)) {
                return false;
            }
        }
        request.setAttribute(StorageHandler.class.getName(), storageHandler);
        return true;
    }
}
