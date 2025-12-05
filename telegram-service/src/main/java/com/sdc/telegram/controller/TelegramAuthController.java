package com.sdc.telegram.controller;

import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateTdlib;
import com.sdc.telegram.service.TelegramAuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

//TODO docs refactor
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TelegramAuthController {
    private final TelegramAuthService telegramAuthService;

    @Operation(summary = "Ввод номера для логина в аккаунт телеграм")
    @PostMapping("/login/phone")
    public void sendPhoneNumber(
            @RequestParam
            final String phone,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/login/phone request with phone:{}, accountId:{}", phone, accountId);
        telegramAuthService.sendPhoneNumber(phone, accountId);
        log.info("Produced response 200 for POST /telegram/login/phone request");
    }

    @Operation(summary = "Ввод кода-подтверждения для логина в аккаунт телеграм")
    @PostMapping("/login/code")
    public void sendAuthCode(
            @RequestParam
            final String code,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/login/code request with accountId:{}", accountId);
        telegramAuthService.sendAuthCode(code, accountId);
        log.info("Produced response 200 for POST /telegram/login/code request");
    }

    @Operation(summary = "Ввод кода-подтверждения для логина в аккаунт телеграм")
    @PostMapping("/login/password")
    public void sendPassword(
            @RequestParam
            final String password,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /telegram/login/password request with accountId:{}", accountId);
        telegramAuthService.sendPassword(password, accountId);
        log.info("Produced response 200 for POST /telegram/login/password request");
    }

    @Operation(summary = "Получение состояния авторизации аккаунта")
    @GetMapping("/state")
    public AuthorizationStateTdlib getAuthorizationState(@RequestParam final String accountId) throws ExecutionException, InterruptedException {
        log.info("Received request GET /auth/state request with accountId:{}", accountId);
        final AuthorizationStateTdlib result = telegramAuthService.getAuthorizationState(accountId);
        log.info("Produced response 200 for GET /auth/state request with body:{}", result);
        return result;
    }

    @Operation(description = "Выход из аккаунта")
    @PostMapping("/logout")
    public void logout(@RequestParam final String accountId) {
        log.info("Received request POST /logout request with accountId:{}", accountId);
        telegramAuthService.logout(accountId);
        log.info("Produced response 200 for POST /logout request");
    }
}
