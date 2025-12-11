package com.sdc.discord.controller;

import com.sdc.discord.domain.dto.bot.DiscordBotInfoDto;
import com.sdc.discord.domain.dto.request.AddBotRequestDto;
import com.sdc.discord.service.DiscordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Main discord controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/api/discord")
@RequiredArgsConstructor
public class DiscordController {
    private final DiscordService discordService;

    @Operation(summary = "Get connected bots")
    @GetMapping("/me/bots")
    public List<DiscordBotInfoDto> getConnectedBots() {
        log.info("Received request GET /api/discord/me/bots");
        final List<DiscordBotInfoDto> result = discordService.getConnectedBots();
        log.info("Produced response 200 for GET /api/discord/me/bots");
        return result;
    }

    @Operation(summary = "Add a new bot to the app")
    @PostMapping("/bots/add")
    public DiscordBotInfoDto addBot(@RequestBody @Valid final AddBotRequestDto request) {
        log.info("Received request POST /api/discord/bots/add");
        final DiscordBotInfoDto result = discordService.addBot(request);
        log.info("Produced response 200 for POST /api/discord/bots/add");
        return result;

    }
}
