package com.sdc.gmail.controller;

import com.sdc.gmail.domain.dto.GmailMessagesResponseDto;
import com.sdc.gmail.service.GmailReaderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@RestController
@RequestMapping("/api/gmail")
@RequiredArgsConstructor
public class GmailReaderController {
    private final GmailReaderService gmailReaderService;

    @GetMapping("/{accountId}/messages")
    @Operation(summary = "Get list of emails")
    public GmailMessagesResponseDto getMessages(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Maximum number of results per page")
            @RequestParam(required = false, defaultValue = "20") final Integer maxResults,

            @Parameter(description = "Page token for pagination")
            @RequestParam(required = false) final String pageToken) throws Exception {
        log.info("Received request GET /api/gmail/{}/messages", accountId);
        final GmailMessagesResponseDto response = gmailReaderService.getMessages(accountId, maxResults, pageToken);
        log.info("Produced response 200 for GET /api/gmail/{}/messages", accountId);
        return response;
    }

    @GetMapping("/{accountId}/unread")
    @Operation(summary = "Get unread emails")
    public GmailMessagesResponseDto getUnreadMessages(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Maximum number of results per page")
            @RequestParam(required = false, defaultValue = "20") final Integer maxResults,

            @Parameter(description = "Page token for pagination")
            @RequestParam(required = false) final String pageToken) throws Exception {
        log.info("Received request GET /api/gmail/{}/unread", accountId);
        final GmailMessagesResponseDto response = gmailReaderService.getUnreadMessages(accountId, maxResults, pageToken);
        log.info("Produced response 200 for GET /api/gmail/{}/unread", accountId);
        return response;
    }

    @PostMapping("/{accountId}/read/{messageId}")
    @Operation(summary = "Mark email as read")
    public void markAsRead(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Message ID", required = true)
            @PathVariable final String messageId) throws Exception {
        log.info("Received request POST /api/gmail/{}/read/{}", accountId, messageId);
        gmailReaderService.markAsRead(accountId, messageId);
        log.info("Produced response 200 for POST /api/gmail/{}/read/{}", accountId, messageId);
    }

    @PostMapping("/{accountId}/unread/{messageId}")
    @Operation(summary = "Mark email as unread")
    public void markAsUnread(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Message ID", required = true)
            @PathVariable final String messageId) throws Exception {
        log.info("Received request POST /api/gmail/{}/unread/{}", accountId, messageId);
        gmailReaderService.markAsUnread(accountId, messageId);
        log.info("Produced response 200 for POST /api/gmail/{}/unread/{}", accountId, messageId);
    }

    @GetMapping("/{accountId}/search")
    @Operation(summary = "Search emails by query")
    public GmailMessagesResponseDto searchMessages(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Search query (e.g., 'from:example@gmail.com', 'subject:important', 'has:attachment')", required = true)
            @RequestParam final String query,

            @Parameter(description = "Maximum number of results per page")
            @RequestParam(required = false, defaultValue = "20") final Integer maxResults,

            @Parameter(description = "Page token for pagination")
            @RequestParam(required = false) final String pageToken) throws Exception {
        log.info("Received request GET /api/gmail/{}/search with query:{}", accountId, query);
        final GmailMessagesResponseDto response = gmailReaderService.searchMessages(accountId, query, maxResults, pageToken);
        log.info("Produced response 200 for GET /api/gmail/{}/search", accountId);
        return response;
    }

    @GetMapping(value = "/{accountId}/attachment/{attachmentId}/image",
            produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Download image attachment")
    public StreamingResponseBody downloadImage(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Message ID", required = true)
            @RequestParam final String messageId,

            @Parameter(description = "Attachment ID", required = true)
            @PathVariable final String attachmentId) {
        log.info("Received request GET /api/gmail/{}/attachment/{}/image", accountId, attachmentId);
        final StreamingResponseBody responseBody = gmailReaderService.downloadImageStream(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /api/gmail/{}/attachment/{}/image", accountId, attachmentId);
        return responseBody;
    }

    @GetMapping(value = "/{accountId}/attachment/{attachmentId}/video",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Download video attachment")
    public StreamingResponseBody downloadVideo(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Message ID", required = true)
            @RequestParam final String messageId,

            @Parameter(description = "Attachment ID", required = true)
            @PathVariable final String attachmentId) {
        log.info("Received request GET /api/gmail/{}/attachment/{}/video", accountId, attachmentId);
        final StreamingResponseBody responseBody = gmailReaderService.downloadVideoStream(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /api/gmail/{}/attachment/{}/video", accountId, attachmentId);
        return responseBody;
    }

    @GetMapping(value = "/{accountId}/attachment/{attachmentId}/document",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Download document attachment")
    public StreamingResponseBody downloadDocument(
            @Parameter(description = "Gmail account ID", required = true)
            @PathVariable final Long accountId,

            @Parameter(description = "Message ID", required = true)
            @RequestParam final String messageId,

            @Parameter(description = "Attachment ID", required = true)
            @PathVariable final String attachmentId) {
        log.info("Received request GET /api/gmail/{}/attachment/{}/document", accountId, attachmentId);
        final StreamingResponseBody responseBody = gmailReaderService.downloadDocumentStream(accountId, messageId, attachmentId);
        log.info("Produced response 200 for GET /api/gmail/{}/attachment/{}/document", accountId, attachmentId);
        return responseBody;
    }
}
