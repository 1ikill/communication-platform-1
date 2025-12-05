package com.sdc.main.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Communication platform type.
 * @since 11.2025
 */
@Getter
@RequiredArgsConstructor
public enum CommunicationPlatformType {
    /**
     * TELEGRAM
     */
    TELEGRAM,

    /**
     * EMAIL
     */
    EMAIL,

    /**
     * VIBER
     */
    VIBER,

    /**
     * WHATSAPP
     */
    WHATSAPP,

    /**
     * TEAMS
     */
    TEAMS;

    public static final String TELEGRAM_NAME = "TELEGRAM";
    public static final String EMAIL_NAME = "EMAIL";
    public static final String VIBER_NAME = "VIBER";
    public static final String WHATSAPP_NAME = "WHATSAPP";
    public static final String TEAMS_NAME = "TEAMS";
}
