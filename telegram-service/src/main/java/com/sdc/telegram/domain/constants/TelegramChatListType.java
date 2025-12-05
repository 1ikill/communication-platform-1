package com.sdc.telegram.domain.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramChatListType {
    /**
     * Main chat list.
     */
    MAIN,

    /**
     * Archive chat list.
     */
    ARCHIVE,

    /**
     * Folder chat list.
     */
    FOLDER;

    public static final String MAIN_NAME = "MAIN";
    public static final String ARCHIVE_NAME = "ARCHIVE";
    public static final String FOLDER_NAME = "FOLDER";
}
