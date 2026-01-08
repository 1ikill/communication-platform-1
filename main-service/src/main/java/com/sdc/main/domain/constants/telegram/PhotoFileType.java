package com.sdc.main.domain.constants.telegram;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PhotoFileType {
    /**
     * Plain photo file.
     */
    PHOTO("plain"),

    /**
     * Profile photo file.
     */
    PROFILE("profile");

    private final String title;
}
