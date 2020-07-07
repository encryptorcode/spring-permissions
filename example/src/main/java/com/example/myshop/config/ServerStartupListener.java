package com.example.myshop.config;

import io.github.encryptorcode.permissions.service.PermissionManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServerStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        PermissionManager.init("com.example.myshop");
    }
}
