package com.sdc.whatsapp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdc.whatsapp.domain.model.WhatsappCredentials;
import com.sdc.whatsapp.repository.WhatsappCredentialsRepository;
import com.sdc.whatsapp.service.WhatsappIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("whatsapp/webhook")
@RequiredArgsConstructor
public class WhatsappWebhookController {

    private final ObjectMapper objectMapper;
    private final WhatsappCredentialsRepository accountRepo;
    private final WhatsappIntegrationService integrationService;

    /**
     * Meta will call this to verify webhook during setup.
     * Each account has its own verify token stored in DB.
     */
    @GetMapping
    public String verify(@RequestParam(value = "hub.mode", required = false) String mode,
                         @RequestParam(value = "hub.verify_token", required = false) String token,
                         @RequestParam(value = "hub.challenge", required = false) String challenge) {

        if (!"subscribe".equals(mode) || token == null || challenge == null) {
            return "invalid request";
        }

        Optional<WhatsappCredentials> accountOpt = accountRepo.findByWebhookVerifyToken(token);
        if (accountOpt.isPresent()) {
            return challenge;
        }

        return "invalid verify token";
    }

    /**
     * Receive webhook notifications from Meta.
     * No signature verification since we removed app-secret.
     */
    @PostMapping
    public String receive(@RequestBody String payload) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            JsonNode entry = root.path("entry");

            if (!entry.isArray() || entry.size() == 0) {
                return "ok";
            }

            for (JsonNode e : entry) {
                JsonNode changes = e.path("changes");
                if (!changes.isArray()) continue;

                for (JsonNode ch : changes) {
                    JsonNode value = ch.path("value");
                    String phoneNumberId = value.path("metadata").path("phone_number_id").asText(null);

                    if (phoneNumberId == null) {
                        continue;
                    }

                    JsonNode messages = value.path("messages");
                    if (messages.isArray()) {
                        for (JsonNode m : messages) {
                            Optional<WhatsappCredentials> accOpt = accountRepo.findByPhoneNumberId(phoneNumberId);
                            if (accOpt.isEmpty()) {
                                continue;
                            }
                            integrationService.saveInboundMessage(phoneNumberId, m);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            //todo
        }
        return "ok";
    }
}