package com.easylabour.walletservice.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Component
public class InternalServiceKeyFilter extends OncePerRequestFilter {

    @Value("${wallet.internal.booking-secret:}")
    private String bookingSecretKey;

    private boolean secureEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(aBytes, bBytes);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        // Apply only to internal endpoints: POST /api/wallet/credit and POST /api/wallet/debit
        if (path != null && (path.endsWith("/api/wallet/credit") || path.endsWith("/api/wallet/debit"))) {
            String internalKey = request.getHeader("X-Internal-Service-Key");

            if (bookingSecretKey == null || bookingSecretKey.isBlank()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal booking service key not configured");
                return;
            }

            if (internalKey == null || !secureEquals(internalKey, bookingSecretKey)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing internal service key");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "booking-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
