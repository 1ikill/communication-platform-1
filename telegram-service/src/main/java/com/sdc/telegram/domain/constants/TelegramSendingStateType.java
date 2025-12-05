package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramSendingStateType {
    /**
     * Pending message sending status.
     */
    PENDING,

    /**
     * Failed message sending status.
     */
    FAILED;

    public static final String PENDING_NAME = "PENDING";
    public static final String FAILED_NAME = "FAILED";
}
