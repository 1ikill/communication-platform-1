package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramAuthorizationStateType {
    /**
     * Authorization state ready.
     */
    READY,

    /**
     * Authorization state wait phone number.
     */
    WAIT_PHONE_NUMBER,

    /**
     * Authorization state wait code.
     */
    WAIT_CODE,

    /**
     * Authorization state wait password.
     */
    WAIT_PASSWORD;

    public static final String READY_NAME = "READY";
    public static final String WAIT_PHONE_NUMBER_NAME = "WAIT_PHONE_NUMBER";
    public static final String WAIT_CODE_NAME = "WAIT_CODE";
    public static final String WAIT_PASSWORD_NAME = "WAIT_PASSWORD";
}
