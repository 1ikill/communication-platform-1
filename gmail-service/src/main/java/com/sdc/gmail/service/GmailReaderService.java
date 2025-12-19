package com.sdc.gmail.service;


import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.sdc.gmail.domain.model.GmailCredentials;
import com.sdc.gmail.domain.dto.AttachmentDto;
import com.sdc.gmail.domain.dto.GmailMessageDto;
import com.sdc.gmail.domain.dto.GmailMessagesResponseDto;
import com.sdc.gmail.repository.GmailCredentialsRepository;
import com.sdc.gmail.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailReaderService {

    private final GmailCredentialsRepository gmailCredentialsRepository;
    private final CryptoUtils cryptoUtils;

    private static final Long MIN_EXPIRES_IN = 60L;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final String GMAIL_USER_ME = "me";
    private static final String MESSAGE_FORMAT_FULL = "full";
    private static final String LABEL_UNREAD = "UNREAD";
    private static final String QUERY_UNREAD = "is:unread";
    private static final String APPLICATION_NAME = "CommunicationPlatform";
    private static final String TOKEN_SERVER_URL = "https://oauth2.googleapis.com/token";
    private static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    private static final String HEADER_FROM = "from";
    private static final String HEADER_TO = "to";
    private static final String HEADER_SUBJECT = "subject";
    private static final String EMPTY_STRING = "";

    /**
     * Get list of all emails with pagination
     * @param accountId Gmail account ID
     * @param maxResults Maximum number of results (default: 20)
     * @param pageToken Token for next page
     * @return List of emails with pagination info
     */
    public GmailMessagesResponseDto getMessages(final Long accountId, final Integer maxResults, final String pageToken) throws Exception {
        return fetchMessages(accountId, null, maxResults, pageToken);
    }

    /**
     * Get list of unread emails
     * @param accountId Gmail account ID
     * @param maxResults Maximum number of results (default: 20)
     * @param pageToken Token for next page
     * @return List of unread emails
     */
    public GmailMessagesResponseDto getUnreadMessages(final Long accountId, final Integer maxResults, final String pageToken) throws Exception {
        return fetchMessages(accountId, QUERY_UNREAD, maxResults, pageToken);
    }

    /**
     * Search messages by query
     * @param accountId Gmail account ID
     * @param query Search query (e.g., "from:example@gmail.com", "subject:important", "has:attachment")
     * @param maxResults Maximum number of results (default: 20)
     * @param pageToken Token for next page
     * @return List of messages matching the query
     */
    public GmailMessagesResponseDto searchMessages(final Long accountId, final String query, final Integer maxResults, final String pageToken) throws Exception {
        return fetchMessages(accountId, query, maxResults, pageToken);
    }

    /**
     * Mark email as read
     * @param accountId Gmail account ID
     * @param messageId Message ID
     */
    public void markAsRead(final Long accountId, final String messageId) throws Exception {
        final GmailCredentials account = validateAndGetAccount(accountId);
        final Gmail gmail = buildGmailService(account);

        final ModifyMessageRequest mods = new ModifyMessageRequest()
                .setRemoveLabelIds(Collections.singletonList(LABEL_UNREAD));

        gmail.users().messages().modify(GMAIL_USER_ME, messageId, mods).execute();
    }

    /**
     * Mark email as unread
     * @param accountId Gmail account ID
     * @param messageId Message ID
     */
    public void markAsUnread(final Long accountId, final String messageId) throws Exception {
        final GmailCredentials account = validateAndGetAccount(accountId);
        final Gmail gmail = buildGmailService(account);

        final ModifyMessageRequest mods = new ModifyMessageRequest()
                .setAddLabelIds(Collections.singletonList(LABEL_UNREAD));

        gmail.users().messages().modify(GMAIL_USER_ME, messageId, mods).execute();
    }

    /**
     * Fetch messages with optional query filter
     * @param accountId Gmail account ID
     * @param query Optional search query
     * @param maxResults Maximum number of results
     * @param pageToken Page token for pagination
     * @return Response with messages and pagination info
     */
    private GmailMessagesResponseDto fetchMessages(final Long accountId, final String query, final Integer maxResults, final String pageToken) throws Exception {
        final GmailCredentials account = validateAndGetAccount(accountId);
        final Gmail gmail = buildGmailService(account);

        final Gmail.Users.Messages.List request = gmail.users().messages().list(GMAIL_USER_ME);

        if (query != null && !query.isEmpty()) {
            request.setQ(query);
        }

        final Integer effectiveMaxResults = maxResults != null ? maxResults : DEFAULT_MAX_RESULTS;
        request.setMaxResults((long) effectiveMaxResults);

        if (pageToken != null && !pageToken.isEmpty()) {
            request.setPageToken(pageToken);
        }

        final ListMessagesResponse response = request.execute();
        final List<Message> messages = fetchFullMessages(gmail, response.getMessages());

        return convertToResponseDto(messages, response.getNextPageToken(), response.getResultSizeEstimate());
    }

    /**
     * Fetch full message details for message summaries
     * @param gmail Gmail service instance
     * @param messageSummaries List of message summaries
     * @return List of full messages with all details
     */
    private List<Message> fetchFullMessages(final Gmail gmail, final List<Message> messageSummaries) {
        final List<Message> messages = new ArrayList<>();
        if (messageSummaries != null) {
            for (final Message messageSummary : messageSummaries) {
                try {
                    final Message fullMessage = gmail.users().messages()
                            .get(GMAIL_USER_ME, messageSummary.getId())
                            .setFormat(MESSAGE_FORMAT_FULL)
                            .execute();
                    messages.add(fullMessage);
                } catch (Exception e) {
                    log.error("Error fetching message data for messageId: {}", messageSummary.getId(), e);
                }
            }
        }
        return messages;
    }

    /**
     * Validate and retrieve Gmail account credentials
     * @param accountId Gmail account ID
     * @return Gmail credentials
     */
    private GmailCredentials validateAndGetAccount(final Long accountId) throws Exception {
        final GmailCredentials account = gmailCredentialsRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        validateAndRefreshToken(account);

        return account;
    }

    /**
     * Validate token expiry and refresh if needed
     * @param account Gmail credentials to validate
     */
    private void validateAndRefreshToken(final GmailCredentials account) throws Exception {
        final String clientId = cryptoUtils.decrypt(account.getClientId());
        final String clientSecret = cryptoUtils.decrypt(account.getClientSecret());
        final String accessToken = cryptoUtils.decrypt(account.getAccessToken());
        final String refreshToken = cryptoUtils.decrypt(account.getRefreshToken());

        final HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final Credential credential = buildCredential(clientId, clientSecret, transport, jsonFactory);
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);

        final Long expiresIn = credential.getExpiresInSeconds();
        if (expiresIn == null || expiresIn <= MIN_EXPIRES_IN) {
            final boolean refreshed = credential.refreshToken();
            if (refreshed) {
                account.setAccessToken(cryptoUtils.encrypt(credential.getAccessToken()));
                account.setTokenExpiry(LocalDateTime.now().plusSeconds(credential.getExpiresInSeconds()));
                gmailCredentialsRepository.save(account);
            } else {
                throw new RuntimeException("Failed to refresh access token. Reauthorization required.");
            }
        }
    }

    /**
     * Build Gmail service instance from credentials
     * @param account Gmail credentials
     * @return Configured Gmail service
     */
    private Gmail buildGmailService(final GmailCredentials account) throws Exception {
        final String clientId = cryptoUtils.decrypt(account.getClientId());
        final String clientSecret = cryptoUtils.decrypt(account.getClientSecret());
        final String accessToken = cryptoUtils.decrypt(account.getAccessToken());
        final String refreshToken = cryptoUtils.decrypt(account.getRefreshToken());

        final HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
        final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final Credential credential = buildCredential(clientId, clientSecret, transport, jsonFactory);
        credential.setAccessToken(accessToken);
        credential.setRefreshToken(refreshToken);

        return new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Build OAuth2 credential for Gmail API
     * @param clientId OAuth client ID
     * @param clientSecret OAuth client secret
     * @param transport HTTP transport
     * @param jsonFactory JSON factory
     * @return Configured credential
     */
    private Credential buildCredential(final String clientId, final String clientSecret,
                                       final HttpTransport transport, final JsonFactory jsonFactory) {
        return new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(transport)
                .setJsonFactory(jsonFactory)
                .setClientAuthentication(new ClientParametersAuthentication(clientId, clientSecret))
                .setTokenServerUrl(new GenericUrl(TOKEN_SERVER_URL))
                .build();
    }

    /**
     * Convert Gmail messages to response DTO
     * @param messages List of Gmail messages
     * @param nextPageToken Token for next page
     * @param resultSizeEstimate Estimated total results
     * @return Response DTO with messages and pagination info
     */
    private GmailMessagesResponseDto convertToResponseDto(final List<Message> messages, final String nextPageToken, final Long resultSizeEstimate) {
        GmailMessagesResponseDto responseDto = new GmailMessagesResponseDto();
        responseDto.setNextPageToken(nextPageToken);
        responseDto.setResultSizeEstimate(resultSizeEstimate);

        List<GmailMessageDto> messageDtos = messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());

        responseDto.setMessages(messageDtos);
        return responseDto;
    }

    /**
     * Convert Gmail message to DTO
     * @param message Gmail message
     * @return Message DTO
     */
    private GmailMessageDto convertToMessageDto(final Message message) {
        final GmailMessageDto dto = new GmailMessageDto();
        dto.setId(message.getId());
        dto.setThreadId(message.getThreadId());
        dto.setLabelIds(message.getLabelIds());
        dto.setSnippet(message.getSnippet());

        final boolean isUnread = message.getLabelIds() != null &&
                message.getLabelIds().contains(LABEL_UNREAD);
        dto.setUnread(isUnread);

        if (message.getInternalDate() != null) {
            dto.setInternalDate(new Date(message.getInternalDate()));
        }

        if (message.getPayload() != null && message.getPayload().getHeaders() != null) {
            message.getPayload().getHeaders().forEach(header -> {
                switch (header.getName().toLowerCase()) {
                    case HEADER_FROM:
                        dto.setFrom(header.getValue());
                        break;
                    case HEADER_TO:
                        dto.setTo(header.getValue());
                        break;
                    case HEADER_SUBJECT:
                        dto.setSubject(header.getValue());
                        break;
                }
            });
        }

        dto.setBody(extractMessageBody(message));
        dto.setAttachments(extractAttachments(message));

        return dto;
    }

    /**
     * Extract text body from Gmail message
     * @param message Gmail message
     * @return Text body content
     */
    private String extractMessageBody(final Message message) {
        try {
            if (message.getPayload() == null) {
                return EMPTY_STRING;
            }

            if (message.getPayload().getParts() != null) {
                return extractBodyFromParts(message.getPayload().getParts());
            }

            if (message.getPayload().getBody() != null &&
                    message.getPayload().getBody().getData() != null) {
                return decodeBase64(message.getPayload().getBody().getData());
            }

            return EMPTY_STRING;
        } catch (Exception e) {
            log.error("Error extracting message body: {}", e.getMessage());
            return EMPTY_STRING;
        }
    }

    /**
     * Extract text body from message parts recursively
     * @param parts List of message parts
     * @return Text body content
     */
    private String extractBodyFromParts(final List<MessagePart> parts) {
        for (final MessagePart part : parts) {
            if (MIME_TYPE_TEXT_PLAIN.equals(part.getMimeType())) {
                if (part.getBody() != null && part.getBody().getData() != null) {
                    return decodeBase64(part.getBody().getData());
                }
            }

            if (part.getParts() != null) {
                final String body = extractBodyFromParts(part.getParts());
                if (!body.isEmpty()) {
                    return body;
                }
            }
        }
        return EMPTY_STRING;
    }

    /**
     * Decode Base64 URL-safe encoded string
     * @param encoded Base64 encoded string
     * @return Decoded string
     */
    private String decodeBase64(final String encoded) {
        try {
            final byte[] decodedBytes = Base64.getUrlDecoder().decode(encoded);
            return new String(decodedBytes, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64 decoding error: {}", e.getMessage());
            return EMPTY_STRING;
        }
    }

    /**
     * Extract attachment metadata from Gmail message
     * @param message Gmail message
     * @return List of attachment DTOs
     */
    private List<AttachmentDto> extractAttachments(final Message message) {
        final List<AttachmentDto> attachments = new ArrayList<>();
        if (message.getPayload() != null && message.getPayload().getParts() != null) {
            extractAttachmentsFromParts(message.getPayload().getParts(), attachments);
        }
        return attachments;
    }

    /**
     * Extract attachments from message parts recursively
     * @param parts List of message parts
     * @param attachments List to collect attachments
     */
    private void extractAttachmentsFromParts(final List<MessagePart> parts, final List<AttachmentDto> attachments) {
        for (final MessagePart part : parts) {
            if (part.getFilename() != null && !part.getFilename().isEmpty() &&
                    part.getBody() != null && part.getBody().getAttachmentId() != null) {
                final AttachmentDto attachment = new AttachmentDto();
                attachment.setAttachmentId(part.getBody().getAttachmentId());
                attachment.setFilename(part.getFilename());
                attachment.setMimeType(part.getMimeType());
                attachment.setSize((long) part.getBody().getSize());
                attachments.add(attachment);
            }

            if (part.getParts() != null) {
                extractAttachmentsFromParts(part.getParts(), attachments);
            }
        }
    }

    /**
     * Create streaming response body for image attachment
     * @param accountId Gmail account ID
     * @param messageId Message ID
     * @param attachmentId Attachment ID
     * @return StreamingResponseBody for image attachment
     */
    public StreamingResponseBody downloadImageStream(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            try {
                final byte[] attachmentData = downloadAttachment(accountId, messageId, attachmentId);
                outputStream.write(attachmentData);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download image attachment", e);
            }
        };
    }

    /**
     * Create streaming response body for video attachment
     * @param accountId Gmail account ID
     * @param messageId Message ID
     * @param attachmentId Attachment ID
     * @return StreamingResponseBody for video attachment
     */
    public StreamingResponseBody downloadVideoStream(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            try {
                final byte[] attachmentData = downloadAttachment(accountId, messageId, attachmentId);
                outputStream.write(attachmentData);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download video attachment", e);
            }
        };
    }

    /**
     * Create streaming response body for document attachment
     * @param accountId Gmail account ID
     * @param messageId Message ID
     * @param attachmentId Attachment ID
     * @return StreamingResponseBody for document attachment
     */
    public StreamingResponseBody downloadDocumentStream(final Long accountId, final String messageId, final String attachmentId) {
        return outputStream -> {
            try {
                final byte[] attachmentData = downloadAttachment(accountId, messageId, attachmentId);
                outputStream.write(attachmentData);
            } catch (Exception e) {
                throw new RuntimeException("Failed to download document attachment", e);
            }
        };
    }

    /**
     * Download attachment data from Gmail
     * @param accountId Gmail account ID
     * @param messageId Message ID
     * @param attachmentId Attachment ID
     * @return Attachment bytes
     */
    private byte[] downloadAttachment(final Long accountId, final String messageId, final String attachmentId) throws Exception {
        final GmailCredentials account = validateAndGetAccount(accountId);
        final Gmail gmail = buildGmailService(account);

        final com.google.api.services.gmail.model.MessagePartBody attachmentBody = gmail.users()
                .messages()
                .attachments()
                .get(GMAIL_USER_ME, messageId, attachmentId)
                .execute();

        final String data = attachmentBody.getData();
        return Base64.getUrlDecoder().decode(data);
    }
}
