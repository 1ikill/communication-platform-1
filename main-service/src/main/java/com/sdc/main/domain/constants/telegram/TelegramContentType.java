package com.sdc.main.domain.constants.telegram;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TelegramContentType {
    /**
     * Text content.
     */
    TEXT,

    /**
     * Photo content.
     */
    IMAGE,

    /**
     * Video content.
     */
    VIDEO,

    /**
     * Document content.
     */
    DOCUMENT;

    public static final String TEXT_NAME = "TEXT";
    public static final String IMAGE_NAME = "IMAGE";
    public static final String DOCUMENT_NAME = "DOCUMENT";
    public static final String VIDEO_NAME = "VIDEO";
}
