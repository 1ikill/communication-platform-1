package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramOriginType {
    /**
     * Chanel message origin.
     */
    CHANEL,

    /**
     * Chat message origin.
     */
    CHAT,

    /**
     * Hidden user message origin.
     */
    HIDDEN_USER,

    /**
     * User message origin.
     */
    USER;

    public static final String CHANEL_NAME = "CHANEL";
    public static final String CHAT_NAME = "CHAT";
    public static final String USER_NAME = "USER";
    public static final String HIDDEN_USER_NAME = "HIDDEN_USER";
}
