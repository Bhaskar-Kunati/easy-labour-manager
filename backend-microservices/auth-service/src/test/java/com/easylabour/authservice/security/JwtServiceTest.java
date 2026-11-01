package com.easylabour.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String testSecret = "404e635266556a586e3272357538782f413f4428472b4b6250655368566d5971";
    private final long testExpiration = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    void testGenerateTokenContainsIdentityAndRoles() {
        UserDetails userDetails = new User("+1234567890", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);

        // Parse claims and verify
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(testSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("+1234567890", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));

        @SuppressWarnings("unchecked")
        java.util.List<String> roles = (java.util.List<String>) claims.get("roles");
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_CUSTOMER"));

        // Verify validity helper
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("+1234567890", jwtService.extractUsername(token));
    }
}
