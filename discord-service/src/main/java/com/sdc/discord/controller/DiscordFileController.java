package com.sdc.discord.controller;

import com.sdc.discord.service.DiscordFileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * Discord file controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/discord/files")
@RequiredArgsConstructor
public class DiscordFileController {
    private final DiscordFileService discordFileService;

    @Operation(summary = "Get image file from discord url")
    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public StreamingResponseBody getImageFromUrl(@RequestParam final String url) {
        log.info("Received request GET /api/discord/files/image");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(url);
        log.info("Produced response 200 for GET /api/discord/files/image");
        return responseBody;
    }

    @Operation(summary = "Get video file from discord url")
    @GetMapping(value = "/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getVideoFromUrl(@RequestParam final String url) {
        log.info("Received request GET /api/discord/files/video");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(url);
        log.info("Produced response 200 for GET /api/discord/files/video");
        return responseBody;
    }

    @Operation(summary = "Get document file from discord url")
    @GetMapping(value = "/document",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getDocumentFromUrl(@RequestParam final String url) {
        log.info("Received request GET /api/discord/files/document");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(url);
        log.info("Produced response 200 for GET /api/discord/files/document");
        return responseBody;
    }
}