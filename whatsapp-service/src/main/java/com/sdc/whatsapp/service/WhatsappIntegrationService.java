package com.sdc.whatsapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.sdc.whatsapp.config.security.CurrentUser;
import com.sdc.whatsapp.domain.model.WhatsappChat;
import com.sdc.whatsapp.domain.model.WhatsappCredentials;
import com.sdc.whatsapp.domain.model.WhatsappMessage;
import com.sdc.whatsapp.integration.WhatsappClientFactory;
import com.sdc.whatsapp.repository.WhatsappChatRepository;
import com.sdc.whatsapp.repository.WhatsappCredentialsRepository;
import com.sdc.whatsapp.repository.WhatsappMessageRepository;
import com.sdc.whatsapp.utils.CryptoUtils;
import com.whatsapp.api.domain.messages.Message;
import com.whatsapp.api.domain.messages.TextMessage;
import com.whatsapp.api.domain.messages.response.MessageResponse;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sdc.whatsapp.domain.constants.MessageDirectionType.INBOUND;
import static com.sdc.whatsapp.domain.constants.MessageDirectionType.OUTBOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsappIntegrationService {
    private final CurrentUser currentUser;
    private final WhatsappCredentialsRepository accountRepo;
    private final WhatsappChatRepository chatRepo;
    private final WhatsappMessageRepository messageRepo;
    private final GraphApiHelper graphApiHelper;
    private final WhatsappClientFactory clientFactory;
    private final CryptoUtils cryptoUtils;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * Register account: validate token + phoneNumberId then save encrypted token.
     * Generates unique webhook verify token per account.
     */
    @Transactional
    public WhatsappCredentials registerAccount(String displayName, String rawAccessToken,
                                               String phoneNumberId, String wabaId) throws Exception {

        boolean valid = graphApiHelper.verifyPhoneNumberId(rawAccessToken, phoneNumberId);
        if (!valid) {
            throw new IllegalArgumentException("Provided token does not have access to phoneNumberId");
        }
        if (accountRepo.findByPhoneNumberId(phoneNumberId).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        WhatsappCredentials account = new WhatsappCredentials();
        account.setUserId(currentUser.getId());
        account.setDisplayName(displayName);
        account.setPhoneNumberId(phoneNumberId);
        account.setWabaId(wabaId);
        account.setAccessToken(cryptoUtils.encrypt(rawAccessToken));
        account.setWebhookVerifyToken(generateUniqueWebhookVerifyToken());
        account.setIsActive(true);

        WhatsappCredentials savedAccount = accountRepo.save(account);

        log.info("Registered new WhatsApp account: {} with phone: {}", displayName, phoneNumberId);


        return savedAccount;
    }

    /**
     * Generate unique webhook verify token for each account.
     */
    private String generateUniqueWebhookVerifyToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get webhook verify token for an account.
     */
    public String getWebhookVerifyToken(Long accountId) {
        return accountRepo.findById(accountId)
                .map(WhatsappCredentials::getWebhookVerifyToken)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    /**
     * Send text message from specified account.
     * Will store outbound message in DB.
     */
    @Transactional
    public MessageResponse sendTextMessage(Long accountId, String toPhoneNumber, String text) throws Exception {
        WhatsappCredentials account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        WhatsappBusinessCloudApi api = clientFactory.createCloudClient(account);

        var message = Message.MessageBuilder.builder()
                .setTo(toPhoneNumber)
                .buildTextMessage(new TextMessage()
                        .setBody(text)
                        .setPreviewUrl(false));

        MessageResponse response = api.sendMessage(account.getPhoneNumberId(), message);

        saveOutboundMessage(account, toPhoneNumber, text, response);

        log.info("Sent message via account {} to {}", accountId, toPhoneNumber);

        return response;
    }

    /**
     * Save outbound message to database.
     */
    private void saveOutboundMessage(WhatsappCredentials account, String toPhoneNumber,
                                     String text, MessageResponse response) {
        try {
            WhatsappChat chat = ensureChat(account.getId(), toPhoneNumber, null);

            WhatsappMessage msg = new WhatsappMessage();
            msg.setWhatsappAccountId(account.getId());
            msg.setWhatsappChatId(chat.getId());
            msg.setDirection(OUTBOUND);
            msg.setSenderWaId(account.getPhoneNumberId());
            msg.setReceiverWaId(toPhoneNumber);
            msg.setType("text");
            msg.setTextBody(text);
            msg.setMessageId(response != null ? response.messages().get(0).id() : null);
            msg.setRawPayload(objectMapper.writeValueAsString(response));
            msg.setCreatedDate(LocalDateTime.now());

            messageRepo.save(msg);

            chat.setLastMessageDate(msg.getCreatedDate());
            chatRepo.save(chat);

        } catch (Exception e) {
            log.error("Failed to save outbound message: {}", e.getMessage(), e);
        }
    }

    /**
     * Save inbound message from webhook into DB.
     */
    @Transactional
    public void saveInboundMessage(String phoneNumberId, JsonNode messageJson) {
        try {
            WhatsappCredentials account = accountRepo.findByPhoneNumberId(phoneNumberId)
                    .orElseThrow(() -> new IllegalArgumentException("Unknown phoneNumberId: " + phoneNumberId));

            String from = messageJson.path("from").asText(null);
            String to = messageJson.path("to").asText(null);
            String id = messageJson.path("id").asText(null);
            String type = messageJson.path("type").asText(null);

            String textBody = null;
            if ("text".equals(type)) {
                textBody = messageJson.path("text").path("body").asText(null);
            }

            WhatsappChat chat = ensureChat(account.getId(), from, null);

            WhatsappMessage msg = new WhatsappMessage();
            msg.setWhatsappAccountId(account.getId());
            msg.setWhatsappChatId(chat.getId());
            msg.setDirection(INBOUND);
            msg.setSenderWaId(from);
            msg.setReceiverWaId(to);
            msg.setMessageId(id);
            msg.setType(type);
            msg.setTextBody(textBody);

            try {
                msg.setRawPayload(objectMapper.writeValueAsString(messageJson));
            } catch (Exception ex) {
                msg.setRawPayload("{\"error\":\"serialization_failed\"}");
            }

            msg.setCreatedDate(LocalDateTime.now());
            messageRepo.save(msg);

            chat.setLastMessageDate(msg.getCreatedDate());
            chatRepo.save(chat);

            log.info("Saved inbound message from {} to account {}", from, account.getId());

        } catch (Exception e) {
            log.error("Failed to save inbound message: {}", e.getMessage(), e);
        }
    }

    /**
     * Ensure a chat row exists for (accountId, contactWaId).
     */
    @Transactional
    public WhatsappChat ensureChat(Long accountId, String contactWaId, String contactDisplay) {
        Optional<WhatsappChat> opt = chatRepo.findByWhatsappAccountIdAndContactWaId(accountId, contactWaId);
        if (opt.isPresent()) {
            return opt.get();
        }

        WhatsappChat chat = new WhatsappChat();
        chat.setWhatsappAccountId(accountId);
        chat.setContactWaId(contactWaId);
        chat.setContactDisplay(contactDisplay);
        chat.setCreatedDate(LocalDateTime.now());

        return chatRepo.save(chat);
    }

    /**
     * List chats for an account with ownership check.
     */
    public List<WhatsappChat> listChats(Long accountId) {
        WhatsappCredentials account = accountRepo.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        return chatRepo.findByWhatsappAccountIdOrderByLastMessageDateDesc(accountId);
    }

    /**
     * Get messages for a chat with ownership check.
     */
    public List<WhatsappMessage> getMessagesForChat(Long chatId) {
        WhatsappChat chat = chatRepo.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        return messageRepo.findByWhatsappChatIdOrderByCreatedDateAsc(chatId);
    }
}