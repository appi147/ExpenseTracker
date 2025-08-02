package com.appi147.expensetracker.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = WebConfig.class)
class WebConfigTest {

    @Autowired
    private WebMvcConfigurer webMvcConfigurer;

    @Test
    void testCorsConfigurerExists() {
        assertThat(webMvcConfigurer).isNotNull();
    }
}


