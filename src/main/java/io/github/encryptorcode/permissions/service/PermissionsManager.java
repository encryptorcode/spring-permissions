package io.github.encryptorcode.permissions.service;

import io.github.encryptorcode.permissions.abstracts.Permission;
import io.github.encryptorcode.permissions.abstracts.PermissionHandler;
import io.github.encryptorcode.permissions.abstracts.PermissionValidator;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Rules
 * 1. Classes should have extended {@link PermissionHandler} and have a single arg constructor to accept StorageHandler
 * 2. Validator methods should have {@link PermissionValidator} annotation on them
 * 3. Methods should be public and return void. And should throw an exception in case of validation error
 * 4. Every time a validation is to be done, a new object will be created.
 */
public class PermissionsManager {

    private static final Logger LOGGER = Logger.getLogger(PermissionsManager.class.getName());
    private static final Map<String, PermissionHandlerData> PERMISSION_HANDLERS;

    static {
        long start = System.nanoTime();
        LOGGER.log(Level.INFO, "Initializing permissions manager at " + start);
        Set<Class<? extends PermissionHandler>> permissionHandlers = ReflectionsHelper.getSubClassesOf(PermissionHandler.class);
        //noinspection unchecked
        PERMISSION_HANDLERS = permissionHandlers.stream()
                .map(Class::getMethods)
                .flatMap(Arrays::stream)
                .filter(method -> method.getAnnotation(PermissionValidator.class) != null)
                .map(method -> new PermissionHandlerData(
                        method.getAnnotation(PermissionValidator.class).value(),
                        (Class<? extends PermissionHandler>) method.getDeclaringClass(),
                        method
                ))
                .collect(Collectors.toMap(PermissionHandlerData::getId, data -> data));
        LOGGER.log(Level.INFO, "Initialising permissions manager completed. Total time: {0} ns", new String[]{String.valueOf(System.nanoTime() - start)});
    }

    /**
     * Purpose of this method is to help initializing this class when invoked.
     * This might help in instances when you might want to initialize this when the server starts.
     */
    public static void init() {
    }

    static boolean invoke(HttpServletRequest request, Permission permission, StorageHandler storageHandler) throws Exception {
        try {
            PermissionHandlerData data = PERMISSION_HANDLERS.get(permission.name());
            PermissionHandler handler = data.getClazz().getConstructor(StorageHandler.class).newInstance(storageHandler);
            Method method = data.getMethod();
            method.setAccessible(true);
            method.invoke(handler, getArgs(method, request, permission));
        } catch (InstantiationException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw (Exception) e.getTargetException();
        }
        return true;
    }

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("^\\$\\{(path|query|header)\\.(.+)}$");

    private static Object[] getArgs(Method method, HttpServletRequest request, Permission permission) {
        List<Object> args = new ArrayList<>();
        SimpleTypeConverter converter = new SimpleTypeConverter();
        String[] providedArgs = permission.args();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            String providedArg = providedArgs[i];
            Matcher matcher = VARIABLE_PATTERN.matcher(providedArg);
            if (matcher.matches()) {
                switch (matcher.group(1)) {
                    case "path":
                        @SuppressWarnings("rawtypes")
                        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                        if (pathVariables != null) {
                            providedArg = (String) pathVariables.get(matcher.group(2));
                        } else {
                            providedArg = null;
                        }
                        break;
                    case "query":
                        providedArg = request.getParameter(matcher.group(2));
                        break;
                    case "header":
                        providedArg = request.getHeader(matcher.group(2));
                        break;
                }
            }

            args.add(converter.convertIfNecessary(providedArg, parameter.getType()));
        }
        return args.toArray(new Object[0]);
    }

}
