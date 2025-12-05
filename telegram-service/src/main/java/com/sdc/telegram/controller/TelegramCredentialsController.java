package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.service.TelegramCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/telegram-credentials")
@RequiredArgsConstructor
public class TelegramCredentialsController {
    private final TelegramCredentialService service;

    @PostMapping("/add")
    public void addCredentials(@RequestBody final TelegramCredentialsCreateDto createDto) throws Exception {
        log.info("Received");
        service.addCredentials(createDto);
        log.info("Produced");
    }
}
