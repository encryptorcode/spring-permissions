package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.PermissionVariable;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

public class PermissionVariableResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PermissionVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        PermissionVariable permissionVariable = parameter.getParameterAnnotation(PermissionVariable.class);
        if (permissionVariable == null) {
            return null;
        }

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String variableName = permissionVariable.value();
        StorageHandler storageHandler = (StorageHandler) request.getAttribute(StorageHandler.class.getName());
        if (storageHandler == null) {
            return null;
        }

        Object value = storageHandler.get(variableName);
        return new SimpleTypeConverter().convertIfNecessary(value, parameter.getParameterType());
    }
}
