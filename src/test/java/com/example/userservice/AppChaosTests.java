package com.example.userservice;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(
        classes = UserServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@Slf4j
@Testcontainers
public class AppChaosTests {
    @LocalServerPort
    private Integer localServerPort;

    private static final Network network = Network.newNetwork();

    private static ToxiproxyClient toxiproxyClient;

    private static Proxy dbProxy = null;

    @Container
    //@ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.2-alpine")
            .withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("testdb")
            .withNetwork(network)
            .withInitScript("db/chaos-init-script.sql");

    @Container
    private static final ToxiproxyContainer toxiproxyContainer = new ToxiproxyContainer("ghcr.io/shopify/toxiproxy:2.5.0")
            .withNetwork(network)
            .withEnv("LOG_LEVEL","debug");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) throws Exception {
        var proxyJdbcUrl = "jdbc:postgresql://%s:%d/%s".formatted(toxiproxyContainer.getHost(), toxiproxyContainer.getMappedPort(8666), postgres.getDatabaseName());
        toxiproxyClient = new ToxiproxyClient(toxiproxyContainer.getHost(),toxiproxyContainer.getControlPort());
        dbProxy = toxiproxyClient.createProxy("postgres", "0.0.0.0:8666", "postgres:5432");

        registry.add("spring.datasource.url", () ->  proxyJdbcUrl);
        //registry.add("spring.datasource.url", () ->  postgres.getJdbcUrl());
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    @Test
    public void canConnectToAppWithToxiProxySetup() {

    }



}
