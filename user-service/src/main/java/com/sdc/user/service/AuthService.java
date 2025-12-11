package com.sdc.user.service;

import com.sdc.user.config.security.JwtTokenProvider;
import com.sdc.user.domain.constants.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Authorization details service.
 * @since 11.2025
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpServletRequest request;

    /**
     * Get the current user's role from JWT in Authorization header.
     * @return RoleType.
     */
    public RoleType getCurrentUserRole() {
        final String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        final String token = header.substring(7);
        var claims = jwtTokenProvider.parseToken(token);
        final String role = claims.get("role", String.class);
        if (role == null) {
            return null;
        }

        return RoleType.valueOf(role);
    }

    /**
     * Get current user id.
     * @return Long user id.
     */
    public Long getCurrentUserId() {
        final String header = request.getHeader("Authorization");
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            return null;
        }

        final String token = header.substring(7);
        var claims = jwtTokenProvider.parseToken(token);

        return Long.valueOf(claims.getSubject());
    }

}
