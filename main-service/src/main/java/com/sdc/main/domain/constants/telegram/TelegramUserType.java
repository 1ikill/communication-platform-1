package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramUserType {
    /**
     * Regular user.
     */
    REGULAR,

    /**
     * Deleted user.
     */
    DELETED,

    /**
     * Unknown user.
     */
    UNKNOWN,

    /**
     * Bot user.
     */
    BOT;

    public static final String REGULAR_NAME = "REGULAR";
    public static final String DELETED_NAME = "DELETED";
    public static final String UNKNOWN_NAME = "UNKNOWN";
    public static final String BOT_NAME = "BOT";
}
