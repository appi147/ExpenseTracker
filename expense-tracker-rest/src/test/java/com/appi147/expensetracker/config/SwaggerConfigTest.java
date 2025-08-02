package com.appi147.expensetracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SwaggerConfig.class)
@TestPropertySource(properties = "server.servlet.context-path=/api")
class SwaggerConfigTest {

    @Autowired
    private OpenAPI openAPI;

    @Test
    void testOpenAPIConfigProperties() {
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Expense Tracker API");
        assertThat(openAPI.getServers()).extracting(Server::getUrl).contains(
                "http://localhost:8080/api",
                "https://personalexpensetracker.xyz/api"
        );
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearerAuth");
    }
}
