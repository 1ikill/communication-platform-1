package com.sdc.main.controller;

import com.sdc.main.domain.dto.discord.request.GetFileRequestDto;
import com.sdc.main.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * File-management controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @Operation(summary = "Get telegram image")
    @GetMapping(value ="/telegram/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getTelegramImage(
            @RequestParam
            final String remoteId,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /files/telegram/image with remoteId:{}, accountId:{}", remoteId, accountId);
        final byte[] result = fileService.getTelegramImage(remoteId, accountId);
        log.info("Produced response 200 for GET /files/telegram/image");
        return result;
    }

    @Operation(summary = "Get telegram video")
    @GetMapping(value= "/telegram/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getTelegramVideo(
            @RequestParam
            final String remoteId,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /files/telegram/video with remoteId:{}, accountId:{}", remoteId, accountId);
        final StreamingResponseBody result = fileService.getTelegramVideo(remoteId, accountId);
        log.info("Produced response 200 for GET /files/telegram/video");
        return result;
    }

    @Operation(summary = "Get telegram document")
    @GetMapping(value = "/telegram/document", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getTelegramDocument(
            @RequestParam
            final String remoteId,
            @RequestParam
            final String accountId) {
        log.info("Received request GET /files/telegram/document with remoteId:{}, accountId:{}", remoteId, accountId);
        final StreamingResponseBody result = fileService.getTelegramDocument(remoteId, accountId);
        log.info("Produced response 200 for GET /files/telegram/document");
        return result;
    }

    @Operation(summary = "Get gmail image")
    @GetMapping(value = "/gmail/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public StreamingResponseBody getGmailImage(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String messageId,
            @RequestParam
            final String attachmentId) {
        log.info("Received request GET /files/gmail/image with accountId:{}, messageId:{}, attachmentId:{}", accountId, messageId, attachmentId);
        final StreamingResponseBody result = fileService.getGmailImage(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /files/gmail/image");
        return result;
    }

    @Operation(summary = "Get gmail video")
    @GetMapping(value = "/gmail/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getGmailVideo(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String messageId,
            @RequestParam
            final String attachmentId) {
        log.info("Received request GET /files/gmail/video with accountId:{}, messageId:{}, attachmentId:{}", accountId, messageId, attachmentId);
        final StreamingResponseBody result = fileService.getGmailVideo(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /files/gmail/video");
        return result;
    }

    @Operation(summary = "Get gmail video")
    @GetMapping(value = "/gmail/document", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getGmailDocument(
            @RequestParam
            final Long accountId,
            @RequestParam
            final String messageId,
            @RequestParam
            final String attachmentId) {
        log.info("Received request GET /files/gmail/document with accountId:{}, messageId:{}, attachmentId:{}", accountId, messageId, attachmentId);
        final StreamingResponseBody result = fileService.getGmailDocument(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /files/gmail/document");
        return result;
    }

    @Operation(summary = "Get discord image")
    @PostMapping(value = "/discord/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public StreamingResponseBody getDiscordImage(
            @RequestBody
            final GetFileRequestDto request) {
        log.info("Received request POST /files/discord/image with request:{}", request);
        final StreamingResponseBody result = fileService.getDiscordImage(request);
        log.info("Produced response 200 for POST /files/discord/image");
        return result;
    }

    @Operation(summary = "Get discord video")
    @PostMapping(value = "/discord/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getDiscordVideo(
            @RequestBody
            final GetFileRequestDto request) {
        log.info("Received request POST /files/discord/video with request:{}", request);
        final StreamingResponseBody result = fileService.getDiscordVideo(request);
        log.info("Produced response 200 for POST /files/discord/video");
        return result;
    }

    @Operation(summary = "Get discord document")
    @PostMapping(value = "/discord/document", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody getDiscordDocument(
            @RequestBody
            final GetFileRequestDto request) {
        log.info("Received request POST /files/discord/document with request:{}", request);
        final StreamingResponseBody result = fileService.getDiscordDocument(request);
        log.info("Produced response 200 for POST /files/discord/document");
        return result;
    }
}
