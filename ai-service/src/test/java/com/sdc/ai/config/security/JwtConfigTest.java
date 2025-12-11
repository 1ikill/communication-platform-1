package com.sdc.ai.config.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JwtConfig.class)
@TestPropertySource(properties = {
    "security.jwt.secret=test-secret-key-for-jwt-token-validation-minimum-256-bits"
})
class JwtConfigTest {

    @Autowired
    private JwtDecoder jwtDecoder;

    @Test
    void jwtDecoder_ShouldBeCreated() {
        // Assert
        assertNotNull(jwtDecoder);
    }

    @Test
    void jwtDecoder_ShouldBeNimbusJwtDecoder() {
        // Assert
        assertNotNull(jwtDecoder);
        assertEquals("org.springframework.security.oauth2.jwt.NimbusJwtDecoder", 
                     jwtDecoder.getClass().getName());
    }
}
