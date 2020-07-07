package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.PermissionHandler;
import io.github.encryptorcode.permissions.annotations.Handler;
import io.github.encryptorcode.permissions.annotations.Permission;
import io.github.encryptorcode.permissions.annotations.Permissions;
import io.github.encryptorcode.permissions.entities.HandlerData;
import io.github.encryptorcode.permissions.entities.PermissionManagerException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is the core of this library that handles running all the permission related operations
 * <p>
 * For writing a permission handler the following rules have to be followed.
 * <ol>
 *     <li> Classes should have extended {@link PermissionHandler} and have a single arg constructor to accept {@link Variables} </li>
 *     <li> Handler methods should have {@link Handler} annotation on them with a unique permission id for each handler </li>
 *     <li> Handler methods should be public and return void. And should throw an exception in case of permission validation error </li>
 *     <li> Every time a validation is to be done, a new object will be created. </li>
 * </ol>
 */
public class PermissionManager {

    private static final Logger LOGGER = Logger.getLogger(PermissionManager.class.getName());
    private static final Map<String, HandlerData> PERMISSION_HANDLERS = new HashMap<>();

    /**
     * Invoke this method to initialise the PermissionManager.
     * Call this method when the server is initializing.
     * Make sure to pass your base package name to scan for all the {@link Permission} and {@link Handler} annotations
     *
     * @param packageNames base package name of classes using {@link Permission} and {@link Handler} annotations
     */
    public static void init(String... packageNames) {
        ReflectionsHelper.init(packageNames);
        Set<Class<? extends PermissionHandler>> permissionHandlers = ReflectionsHelper.getSubClassesOf(PermissionHandler.class);
        validateHandlers(permissionHandlers);

        Set<Method> permissionVariableMethods = ReflectionsHelper.getMethodsAnnotatedWith(Permission.class);
        validatePermissionUsages(permissionVariableMethods);
    }

    /**
     * Validates all permission handler implementations
     *
     * @param permissionHandlers permission handlers
     */
    private static void validateHandlers(Set<Class<? extends PermissionHandler>> permissionHandlers) {
        for (Class<? extends PermissionHandler> permissionHandler : permissionHandlers) {
            Method[] methods = permissionHandler.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(Handler.class) != null) {
                    if (method.getReturnType() != void.class) {
                        throw new PermissionManagerException("Failed to initialize PermissionManager.\n" +
                                "Handler methods should have void as return type.\n" +
                                "Method: " + method);
                    }
                    Handler handler = method.getAnnotation(Handler.class);
                    HandlerData handlerData = new HandlerData(handler.id(), method);
                    HandlerData existingData = PERMISSION_HANDLERS.put(handler.id(), handlerData);

                    if (existingData != null) {
                        throw new PermissionManagerException("Failed to initialize PermissionManager.\n" +
                                "Permission handler is defined twice: " + handler.id());
                    }
                    validateHandlerConstructor(method.getDeclaringClass());
                }
            }
        }
    }

    /**
     * Validates constructor implementations of permission handler implementation
     *
     * @param handlerClazz permission handler class
     */
    private static void validateHandlerConstructor(Class<?> handlerClazz) {
        boolean hasVariableHandlerConstructor = false;
        Constructor<?>[] constructors = handlerClazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Parameter[] parameters = constructor.getParameters();
            if (parameters.length == 1 && parameters[0].getType() == Variables.class) {
                hasVariableHandlerConstructor = true;
                break;
            }
        }
        if (!hasVariableHandlerConstructor) {
            throw new PermissionManagerException("Failed to initialize PermissionManager.\n" +
                    "Constructor with variables is not found.\n" +
                    "Class: " + handlerClazz);
        }
    }

    /**
     * Validates usages of {@link Permission @Permission} annotations in controllers
     *
     * @param permissionVariableMethods method in which @Permission annotation is being used
     */
    private static void validatePermissionUsages(Set<Method> permissionVariableMethods) {
        for (Method method : permissionVariableMethods) {
            Permission permission = method.getAnnotation(Permission.class);
            if (permission != null) {
                validatePermissionAnnotationUsage(method, permission);
            }
            Permissions permissions = method.getAnnotation(Permissions.class);
            if (permissions != null) {
                for (Permission subPermission : permissions.value()) {
                    validatePermissionAnnotationUsage(method, subPermission);
                }
            }
        }
    }

    /**
     * Validates individual permission annotation usage
     *
     * @param method     method where permission annotation is being used
     * @param permission annotation class
     */
    private static void validatePermissionAnnotationUsage(Method method, Permission permission) {
        HandlerData handlerData = PERMISSION_HANDLERS.get(permission.id());
        if (handlerData == null) {
            throw new PermissionManagerException("Failed to initialize PermissionManager.\n" +
                    "Invalid permission id specified in controller method.\n" +
                    "Method: " + method);
        }

        if (handlerData.getMethod().getParameters().length != permission.args().length) {
            throw new PermissionManagerException("Failed to initialize PermissionManager.\n" +
                    "Wrong number of arguments passed in controller annotation\n" +
                    "Annotation: " + permission + "\n" +
                    "Method: " + method);
        }
    }

    /**
     * This method will be invoked once for each request if there are any permissions to be validated
     *
     * @param request     request object
     * @param response    response object
     * @param permissions list of permissions to be validated
     * @return boolean return in case a proper error handler is registered (Currently not supported)
     * @throws Exception Exception if any thrown by the permission handler implementation
     */
    static boolean handle(HttpServletRequest request, HttpServletResponse response, List<Permission> permissions) throws Exception {
        Variables variables = new Variables();
        for (Permission permission : permissions) {
            if (!validate(request, permission, variables)) {
                return false;
            }
        }
        request.setAttribute(Variables.class.getName(), variables);
        return true;
    }

    /**
     * This method will be invoked for each permission to be validated
     *
     * @param request    request object
     * @param permission permission
     * @param variables  variables
     * @return always true, might support returning false in the future, if we support error handlers
     * @throws Exception exception thrown from your implementation of {@link PermissionHandler}
     */
    private static boolean validate(HttpServletRequest request, Permission permission, Variables variables) throws Exception {
        try {
            HandlerData data = PERMISSION_HANDLERS.get(permission.id());
            PermissionHandler handler = (PermissionHandler) data.getMethod().getDeclaringClass().getConstructor(Variables.class).newInstance(variables);
            Method method = data.getMethod();
            method.setAccessible(true);
            method.invoke(handler, getArgs(method, request, permission));
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "This exception should never happen as all the pre-checks are already done in init method. Please report this to the developer of this library.", e);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getTargetException();
        }
        return true;
    }

    /**
     * This method helps get generate list of arguments for invoking the handler method
     *
     * @param method     method to be invoked
     * @param request    request object
     * @param permission permission annotation to get list of arguments
     * @return list of objects to be passed as arguments
     */
    private static Object[] getArgs(Method method, HttpServletRequest request, Permission permission) {
        List<Object> args = new ArrayList<>();
        SimpleTypeConverter converter = new SimpleTypeConverter();
        String[] providedArgs = permission.args();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String providedArg = getArgument(request, providedArgs[i]);
            args.add(converter.convertIfNecessary(providedArg, parameter.getType()));
        }
        return args.toArray(new Object[0]);
    }

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\$\\{(path|param|query|header|cookie)\\.(.+)}$");

    /**
     * Gets argument value, If the argument is of one of the below given formats it's relevant value will be taken
     * <ul>
     *     <li> {@code ${path.name}} - Path value taken from the url</li>
     *     <li> {@code ${query.name}} or {@code ${param.name}} - Request param value</li>
     *     <li> {@code ${header.name}} - Header value</li>
     *     <li> {@code ${cookie.name}} - Cookie value</li>
     * </ul>
     *
     * @param request  request object
     * @param argument argument string given in {@link Permission @Permission} annotation in controller
     * @return actual value for the argument
     */
    private static String getArgument(HttpServletRequest request, String argument) {
        Matcher matcher = VARIABLE_PATTERN.matcher(argument);
        if (matcher.matches()) {
            switch (matcher.group(1)) {
                case "path":
                    return getPathVariable(request, matcher.group(2));
                case "query":
                case "param":
                    return getQueryParam(request, matcher.group(2));
                case "header":
                    return getHeader(request, matcher.group(2));
                case "cookie":
                    return getCookie(request, matcher.group(2));
            }
        }
        return argument;
    }

    private static String getPathVariable(HttpServletRequest request, String name) {
        //noinspection unchecked
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables != null) {
            return pathVariables.get(name);
        } else {
            return null;
        }
    }

    private static String getQueryParam(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    private static String getHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }

    private static String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
