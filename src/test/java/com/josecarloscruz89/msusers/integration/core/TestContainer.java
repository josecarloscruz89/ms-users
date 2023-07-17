package com.josecarloscruz89.msusers.integration.core;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public class TestContainer implements BeforeAllCallback {

    private static final PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres:14.5")
            .withDatabaseName("users")
            .withUsername("admin")
            .withPassword("admin");

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        if (!postgresSQLContainer.isRunning()) {
            postgresSQLContainer.setPortBindings(List.of("5454:5432"));
            postgresSQLContainer.start();
        }
    }
}