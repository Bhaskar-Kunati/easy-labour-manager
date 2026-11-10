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
import java.util.List;

@Component
public class InternalServiceKeyFilter extends OncePerRequestFilter {

    @Value("${wallet.internal.secret-key:}")
    private String internalServiceKey;

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

            if (internalServiceKey == null || internalServiceKey.isBlank()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal service key not configured");
                return;
            }

            if (internalKey == null || !internalKey.equals(internalServiceKey)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing internal service key");
                return;
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    "internal-service",
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_INTERNAL_SERVICE"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
