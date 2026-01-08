package com.sdc.discord.service;

import com.sdc.discord.domain.dto.request.GetFileRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
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
        final GetFileRequestDto requestDto = new GetFileRequestDto("https://example.com/test.jpg");

        StreamingResponseBody result = discordFileService.getFileFromUrl(requestDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle valid URL")
    void testGetFileFromUrlWithValidUrl() {
        final GetFileRequestDto requestDto = new GetFileRequestDto("https://cdn.discordapp.com/attachments/123/456/file.png");

        StreamingResponseBody result = discordFileService.getFileFromUrl(requestDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should create streaming body for different URL formats")
    void testGetFileFromUrlDifferentFormats() {
        GetFileRequestDto[] urls = {
            new GetFileRequestDto("https://example.com/file.jpg"),
            new GetFileRequestDto("https://cdn.discord.com/avatars/123/abc.png"),
            new GetFileRequestDto("http://example.com/document.pdf")
        };

        for (GetFileRequestDto url : urls) {
            StreamingResponseBody result = discordFileService.getFileFromUrl(url);
            assertNotNull(result, "Failed for URL: " + url);
        }
    }

    @Test
    @DisplayName("Should handle URLs with query parameters")
    void testGetFileFromUrlWithQueryParameters() {
        final GetFileRequestDto requestDto = new GetFileRequestDto("https://example.com/file.jpg?size=large&format=png");

        StreamingResponseBody result = discordFileService.getFileFromUrl(requestDto);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should handle URLs with special characters")
    void testGetFileFromUrlWithSpecialCharacters() {
        final GetFileRequestDto requestDto = new GetFileRequestDto("https://example.com/path%20with%20spaces/file.jpg");

        StreamingResponseBody result = discordFileService.getFileFromUrl(requestDto);

        assertNotNull(result);
    }
}
