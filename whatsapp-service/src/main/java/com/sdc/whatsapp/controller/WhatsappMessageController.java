package com.sdc.whatsapp.controller;


import com.sdc.whatsapp.domain.model.WhatsappChat;
import com.sdc.whatsapp.domain.model.WhatsappMessage;
import com.sdc.whatsapp.service.WhatsappIntegrationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/whatsapp")
@RequiredArgsConstructor
public class WhatsappMessageController {

    private final WhatsappIntegrationService integrationService;

    @PostMapping("/accounts/{accountId}/send")
    public void sendText(@PathVariable Long accountId, @RequestBody SendTextRequest req) throws Exception {
        integrationService.sendTextMessage(accountId, req.getTo(), req.getText());
    }

    @GetMapping("/accounts/{accountId}/chats")
    public List<WhatsappChat> listChats(@PathVariable Long accountId) {
        List<WhatsappChat> chats = integrationService.listChats(accountId);
        return chats;
    }

    @GetMapping("/chats/{chatId}/messages")
    public List<WhatsappMessage> getMessages(@PathVariable Long chatId) {
        return integrationService.getMessagesForChat(chatId);
    }

    @Getter
    public static class SendTextRequest {
        private String to;
        private String text;
    }
}
