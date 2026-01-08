package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.request.GetFileRequestDto;
import com.sdc.discord.service.DiscordFileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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
    @PostMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public StreamingResponseBody getImageFromUrl(
            @Valid
            @RequestBody
            final GetFileRequestDto request) {
        log.info("Received request POST /api/discord/files/image");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(request);
        log.info("Produced response 200 for POST /api/discord/files/image");
        return responseBody;
    }

    @Operation(summary = "Get video file from discord url")
    @PostMapping(value = "/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getVideoFromUrl(
            @Valid
            @RequestBody
            final GetFileRequestDto request
    ) {
        log.info("Received request POST /api/discord/files/video");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(request);
        log.info("Produced response 200 for POST /api/discord/files/video");
        return responseBody;
    }

    @Operation(summary = "Get document file from discord url")
    @PostMapping(value = "/document",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getDocumentFromUrl(
            @Valid
            @RequestBody
            final GetFileRequestDto request) {
        log.info("Received request POST /api/discord/files/document");
        final StreamingResponseBody responseBody = discordFileService.getFileFromUrl(request);
        log.info("Produced response 200 for POST /api/discord/files/document");
        return responseBody;
    }
}