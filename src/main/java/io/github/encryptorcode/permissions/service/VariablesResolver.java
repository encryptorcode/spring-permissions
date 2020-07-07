package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.annotations.HandlerVariable;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * This implementation helps set values for arguments of controller that are annotated with {@link HandlerVariable @HandlerVariable} annotation
 */
public class VariablesResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(HandlerVariable.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HandlerVariable handlerVariable = parameter.getParameterAnnotation(HandlerVariable.class);
        if (handlerVariable == null) {
            return null;
        }

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String variableName = handlerVariable.value();
        Variables variables = (Variables) request.getAttribute(Variables.class.getName());
        if (variables == null) {
            return null;
        }

        Object value = variables.get(variableName);
        return new SimpleTypeConverter().convertIfNecessary(value, parameter.getParameterType());
    }
}
