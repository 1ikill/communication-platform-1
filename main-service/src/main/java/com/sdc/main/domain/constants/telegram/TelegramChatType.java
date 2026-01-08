package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramChatType {
    /**
     * Private chat type.
     */
    PRIVATE,

    /**
     * Group chat type.
     */
    BASIC_GROUP,

    /**
     * Supergroup chat type.
     */
    SUPERGROUP;

    public static final String PRIVATE_NAME = "PRIVATE";
    public static final String BASIC_GROUP_NAME = "BASIC_GROUP";
    public static final String SUPERGROUP_NAME = "SUPERGROUP";
}
