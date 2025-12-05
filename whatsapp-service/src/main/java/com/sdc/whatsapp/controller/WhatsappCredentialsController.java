package com.sdc.whatsapp.controller;

import com.sdc.whatsapp.domain.dto.request.RegisterAccountRequestDto;
import com.sdc.whatsapp.domain.model.WhatsappCredentials;
import com.sdc.whatsapp.repository.WhatsappCredentialsRepository;
import com.sdc.whatsapp.service.WhatsappIntegrationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/whatsapp/accounts")
@RequiredArgsConstructor
public class WhatsappCredentialsController {

    private final WhatsappIntegrationService integrationService;
    private final WhatsappCredentialsRepository accountRepo;

    @PostMapping("/register")
    public WhatsappCredentials register(@RequestBody RegisterAccountRequestDto request) throws Exception {
        return integrationService.registerAccount(
                request.getDisplayName(),
                request.getAccessToken(),
                request.getPhoneNumberId(),
                request.getWabaId()
        );
    }

}