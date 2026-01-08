package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramAuthenticationCodeType {
    /**
     * Authentication code sms type.
     */
    SMS,

    /**
     * Authentication code telegram message type.
     */
    TELEGRAM_MESSAGE;

    public static final String SMS_NAME = "SMS";
    public static final String TELEGRAM_MESSAGE_NAME = "TELEGRAM_MESSAGE";
}
