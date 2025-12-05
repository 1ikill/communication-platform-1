package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramReplyToType {
    /**
     * Reply to message.
     */
    MESSAGE;

    public static final String MESSAGE_NAME = "MESSAGE";
}
