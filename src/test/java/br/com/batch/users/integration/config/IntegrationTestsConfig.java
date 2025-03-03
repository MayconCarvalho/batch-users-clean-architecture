package br.com.batch.users.integration.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Testcontainers // Enable Test containers support
@TestConfiguration
public abstract class IntegrationTestsConfig {

    @LocalServerPort
    private Integer port; // Inject the random port used by the embedded server

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Container
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER = new PostgreSQLContainer<>(
            "postgres:17.4-alpine" // Use the PostgreSQL 17.4-alpine image
    );

    @Container
    private static final RabbitMQContainer RABBITMQ_CONTAINER = new RabbitMQContainer(
            "rabbitmq:4.0.7-management-alpine" // Use the RabbitMQ 4.1.0-management-alpine image
    );

    @Container
    private static final WireMockContainer wireMockContainer = new WireMockContainer(
            "wiremock/wiremock:3.11.0-alpine" // Use the WireMock 3.11.0-alpine image
    );

    @BeforeAll
    public static void setUpContainers() {
        // Start the PostgreSQL and RabbitMQ containers before all tests
        POSTGRES_CONTAINER.start();
        RABBITMQ_CONTAINER.start();
        startWireMock();
    }

    @AfterAll
    public static void tearDownContainers() {
        // Stop the PostgreSQL and RabbitMQ containers after all tests
        POSTGRES_CONTAINER.stop();
        RABBITMQ_CONTAINER.stop();
    }

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        // Dynamically set the properties for the database and RabbitMQ connections
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
        registry.add("spring.rabbitmq.host", RABBITMQ_CONTAINER::getHost);
        registry.add("spring.rabbitmq.port", RABBITMQ_CONTAINER::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBITMQ_CONTAINER::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBITMQ_CONTAINER::getAdminPassword);
    }

    private static void startWireMock() {
        wireMockContainer.start();
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getMappedPort(8080));
        wireMockContainer.getBaseUrl();
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    public static <T> List<T> readJsonListFile(String filePath, TypeReference<List<T>> typeReference) throws IOException {
        return objectMapper.readValue(readFromResource(filePath), typeReference);
    }

    public static <T> T readJsonObjectFile(String filePath, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(readFromResource(filePath), typeReference);
    }

    private static String readFromResource(String filePath) throws IOException {
        return IOUtils.resourceToString(filePath, StandardCharsets.UTF_8);
    }

}