package com.sdc.discord.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.net.URL;

/**
 * Service for Discord files.
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordFileService {
    /**
     * Get Discord file from Discord file url.
     * @param url Discord file url.
     * @return StreamingResponseBody with file.
     */
    public StreamingResponseBody getFileFromUrl(final String url) {
        return outputStream -> {
            final URL fileUrl = new URL(url);
            try (InputStream inputStream = fileUrl.openStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        };
    }
}
