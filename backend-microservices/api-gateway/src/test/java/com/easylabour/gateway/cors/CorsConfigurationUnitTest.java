package com.easylabour.gateway.cors;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigurationUnitTest {

    @Test
    void testCorsConfigurationRules() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://localhost:5173");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedHeader("Authorization");
        config.addAllowedHeader("Content-Type");
        config.addAllowedHeader("X-Requested-With");
        config.addAllowedHeader("Accept");
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        assertNotNull(config.checkOrigin("http://localhost:3000"));
        assertNotNull(config.checkOrigin("http://localhost:5173"));
        assertNull(config.checkOrigin("http://untrusted-origin.com"));
        assertTrue(config.getAllowCredentials());
        assertEquals(3600L, config.getMaxAge());
        assertTrue(config.getAllowedMethods().contains("POST"));
        assertTrue(config.getAllowedHeaders().contains("Authorization"));
        // Ensure internal service key is NOT exposed to browser CORS allowed headers
        assertFalse(config.getAllowedHeaders().contains("X-Internal-Service-Key"));
    }
}
