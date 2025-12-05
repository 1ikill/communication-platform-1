package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramMessageSenderType {
    /**
     * Chat message-sender.
     */
    CHAT,

    /**
     * User message-sender.
     */
    USER;

    public static final String CHAT_NAME = "CHAT";
    public static final String USER_NAME = "USER";
}
