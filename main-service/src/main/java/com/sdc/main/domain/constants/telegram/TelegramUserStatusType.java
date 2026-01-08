package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramUserStatusType {
    /**
     * User status online.
     */
    ONLINE,

    /**
     * User status - offline.
     */
    OFFLINE,

    /**
     * User status - recently.
     */
    RECENTLY,

    /**
     * User status - last week.
     */
    LAST_WEEK,

    /**
     * User status - last month.
     */
    LAST_MONTH,

    /**
     * Empty user status.
     */
    EMPTY;

    public static final String ONLINE_NAME = "ONLINE";
    public static final String OFFLINE_NAME = "OFFLINE";
    public static final String RECENTLY_NAME = "RECENTLY";
    public static final String LAST_WEEK_NAME = "LAST_WEEK";
    public static final String LAST_MONTH_NAME = "LAST_MONTH";
    public static final String EMPTY_NAME = "EMPTY";
}
