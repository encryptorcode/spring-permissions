package com.example.myshop.config;

import com.example.myshop.common.App;
import com.example.myshop.entities.User;
import com.example.myshop.services.UserService;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String currentUserId = getCurrentUserId(request);
        if (currentUserId != null) {
            User currentUser = new UserService().getUser(Long.parseLong(currentUserId));
            request.setAttribute(App.USER_ATTRIBUTE, currentUser);
        }
        return true;
    }

    private String getCurrentUserId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (App.AUTH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
