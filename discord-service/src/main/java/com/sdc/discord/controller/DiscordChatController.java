package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.chat.DiscordPrivateChatDto;
import com.sdc.discord.service.DiscordPrivateChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/discord/bots/{botId}/chats")
@RequiredArgsConstructor
public class DiscordPrivateChatController {

    private final DiscordPrivateChatService chatService;

    @GetMapping
    public List<DiscordPrivateChatDto> getChats(@PathVariable Long botId) throws Exception {
        List<DiscordPrivateChatDto> chats = chatService.getChats(botId);
        return chats;
    }

    @GetMapping("/search")
    public List<DiscordPrivateChatDto> searchChats(
            @PathVariable Long botId,
            @RequestParam String query) throws Exception {
        List<DiscordPrivateChatDto> chats = chatService.searchChats(botId, query);
        return chats;
    }

    @GetMapping("/{channelId}")
    public DiscordPrivateChatDto getChat(
            @PathVariable Long botId,
            @PathVariable String channelId) throws Exception {

        DiscordPrivateChatDto chat = chatService.getChat(botId, channelId);
        return chat;
    }
}
