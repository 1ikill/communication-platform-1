package com.sdc.discord.config.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Current user session data component.
 * @since 11.2025
 */
@Component
public class CurrentUser {

    public Long getId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return Long.valueOf(jwt.getSubject());
    }

    public String getUsername() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getClaimAsString("username");
    }

    public String getRole() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        return jwt.getClaimAsString("role");
    }
}
