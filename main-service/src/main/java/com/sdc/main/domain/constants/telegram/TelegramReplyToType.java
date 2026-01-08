package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramReplyToType {
    /**
     * Reply to message.
     */
    MESSAGE;

    public static final String MESSAGE_NAME = "MESSAGE";
}
