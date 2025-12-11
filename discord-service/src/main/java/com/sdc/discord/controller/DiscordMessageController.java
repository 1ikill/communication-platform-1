package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.message.DiscordPrivateMessageDto;
import com.sdc.discord.service.DiscordPrivateMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discord/bots/{botId}/direct-messages")
@RequiredArgsConstructor
@Validated
public class DiscordPrivateMessageController {

    private final DiscordPrivateMessageService dmService;

    /**
     * Получить историю сообщений
     */
    @GetMapping("/channels/{channelId}/history")
    public List<DiscordPrivateMessageDto> getHistory(
            @PathVariable Long botId,
            @PathVariable String channelId,
            @RequestParam(defaultValue = "50") int limit) {
        List<DiscordPrivateMessageDto> messages;
        messages = dmService.getMessageHistoryFromDb(botId, channelId, limit); //todo rename ref
        return messages;
    }

    /**
     * Поиск по сообщениям
     */
    @GetMapping("/channels/{channelId}/search")
    public List<DiscordPrivateMessageDto> searchMessages(
            @PathVariable Long botId,
            @PathVariable String channelId,
            @RequestParam String query) {
        List<DiscordPrivateMessageDto> results =
                dmService.searchMessages(botId, channelId, query);

        return results;
    }

    @DeleteMapping("/messages/{messageId}")
    public void deleteMessage(
            @PathVariable Long botId,
            @PathVariable Long messageId) throws Exception {
        dmService.deleteMessage(botId, messageId);
    }

    @PutMapping("/messages/{messageId}")
    public void updateMessage(
            @PathVariable Long botId,
            @PathVariable Long messageId,
            @RequestParam String updatedMessage) throws Exception {
        dmService.updateMessage(botId, messageId, updatedMessage);
    }

}
