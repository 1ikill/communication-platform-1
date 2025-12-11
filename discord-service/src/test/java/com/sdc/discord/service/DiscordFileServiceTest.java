package com.sdc.discord.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DiscordFileService}
 */
@ExtendWith(MockitoExtension.class)
class DiscordFileServiceTest {

    @InjectMocks
    private DiscordFileService discordFileService;

    @Test
    @DisplayName("Should return StreamingResponseBody")
    void testGetFileFromUrlReturnsStreamingResponseBody() {
        String testUrl = "https://example.com/test.jpg";

        StreamingResponseBody result = discordFileService.getFileFromUrl(testUrl);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle valid URL")
    void testGetFileFromUrlWithValidUrl() {
        String testUrl = "https://cdn.discordapp.com/attachments/123/456/file.png";

        StreamingResponseBody result = discordFileService.getFileFromUrl(testUrl);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should create streaming body for different URL formats")
    void testGetFileFromUrlDifferentFormats() {
        String[] urls = {
            "https://example.com/file.jpg",
            "https://cdn.discord.com/avatars/123/abc.png",
            "http://example.com/document.pdf"
        };

        for (String url : urls) {
            StreamingResponseBody result = discordFileService.getFileFromUrl(url);
            assertNotNull(result, "Failed for URL: " + url);
        }
    }

    @Test
    @DisplayName("Should handle URLs with query parameters")
    void testGetFileFromUrlWithQueryParameters() {
        String testUrl = "https://example.com/file.jpg?size=large&format=png";

        StreamingResponseBody result = discordFileService.getFileFromUrl(testUrl);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle URLs with special characters")
    void testGetFileFromUrlWithSpecialCharacters() {
        String testUrl = "https://example.com/path%20with%20spaces/file.jpg";

        StreamingResponseBody result = discordFileService.getFileFromUrl(testUrl);

        assertNotNull(result);
    }
}
