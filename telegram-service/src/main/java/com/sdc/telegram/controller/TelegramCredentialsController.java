package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.service.TelegramCredentialService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Telegram credentials
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/telegram-credentials")
@RequiredArgsConstructor
public class TelegramCredentialsController {
    private final TelegramCredentialService service;

    @PostMapping("/add")
    @Operation(summary = "Add telegram credentials")
    public void addCredentials(@RequestBody final TelegramCredentialsCreateDto createDto) throws Exception {
        log.info("Received request POST /telegram-credentials/add");
        service.addCredentials(createDto);
        log.info("Produced response 200 for POST /telegram-credentials/add");
    }
}
