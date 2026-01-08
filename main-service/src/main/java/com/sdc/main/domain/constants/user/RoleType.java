package com.sdc.main.domain.constants.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User role type enum.
 * @since 10.2025
 */
@Getter
@AllArgsConstructor
public enum RoleType {
    /**
     * Administrator.
     */
    ADMIN,

    /**
     * Default user.
     */
    USER
}
