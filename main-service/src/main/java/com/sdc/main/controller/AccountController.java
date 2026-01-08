package com.sdc.main.controller;

import com.sdc.main.domain.dto.ai.ContactProfileCreateDto;
import com.sdc.main.domain.dto.ai.ContactProfileDto;
import com.sdc.main.domain.dto.ai.ContactProfilePatchDto;
import com.sdc.main.domain.dto.discord.bot.DiscordBotInfoDto;
import com.sdc.main.domain.dto.discord.request.AddBotRequestDto;
import com.sdc.main.domain.dto.gmail.GmailAccountInfoDto;
import com.sdc.main.domain.dto.telegram.TelegramAccountDto;
import com.sdc.main.domain.dto.telegram.TelegramCredentialsCreateDto;
import com.sdc.main.domain.dto.telegram.auth.AuthorizationStateTdlib;
import com.sdc.main.domain.dto.telegram.user.UserTdlibDto;
import com.sdc.main.domain.dto.user.AuthRequestDto;
import com.sdc.main.domain.dto.user.UserCreateDto;
import com.sdc.main.domain.dto.user.UserDto;
import com.sdc.main.domain.dto.user.UserPatchDto;
import com.sdc.main.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Account-management controller.
 * @since 12.2025
 */
@Slf4j
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "Get telegram account info")
    @GetMapping("/telegram/info")
    public UserTdlibDto getTelegramAccountInfo(
            @RequestParam
            final String accountId) {
        log.info("Received request GET /accounts/telegram/info with accountId:{}", accountId);
        final UserTdlibDto result = accountService.getTelegramAccountInfo(accountId);
        log.info("Produced response 200 for GET /accounts/telegram/info with body:{}", result);
        return result;
    }

    @Operation(summary = "Get connected telegram accounts info")
    @GetMapping("/telegram/connected/info")
    public List<TelegramAccountDto> getConnectedTelegramAccounts() {
        log.info("Received request GET /accounts/telegram/connected/info");
        final List<TelegramAccountDto> result = accountService.getConnectedTelegramAccounts();
        log.info("Produced response 200 for GET /accounts/telegram/connected/info with body:{}", result);
        return result;
    }

    @Operation(summary = "Submit telegram phone number")
    @PostMapping("/telegram/auth/phone")
    public void submitTelegramPhoneNumber(
            @RequestParam
            final String phone,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /accounts/telegram/auth/phone with phone:{}, accountId:{}", phone, accountId);
        accountService.submitTelegramPhoneNumber(phone, accountId);
        log.info("Produced response 200 for POST /accounts/telegram/auth/phone");
    }

    @Operation(summary = "Submit telegram auth code")
    @PostMapping("/telegram/auth/code")
    public void submitTelegramCode(
            @RequestParam
            final String code,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /accounts/telegram/auth/code with accountId:{}", accountId);
        accountService.submitTelegramCode(code, accountId);
        log.info("Produced response 200 for POST /accounts/telegram/auth/code");
    }

    @Operation(summary = "Submit telegram password")
    @PostMapping("/telegram/auth/password")
    public void submitTelegramPassword(
            @RequestParam
            final String password,
            @RequestParam
            final String accountId) {
        log.info("Received request POST /accounts/telegram/auth/password with accountId:{}", accountId);
        accountService.submitTelegramPassword(password, accountId);
        log.info("Produced response 200 for POST /accounts/telegram/auth/password");
    }

    @Operation(summary = "Get telegram account authorization state")
    @GetMapping("/telegram/auth/state")
    public AuthorizationStateTdlib getTelegramAccountAuthState(
            @RequestParam
            final String accountId) {
        log.info("Received request GET /accounts/telegram/auth/state with accountId:{}", accountId);
        final AuthorizationStateTdlib result = accountService.getTelegramAccountAuthState(accountId);
        log.info("Produced response 200 for GET /accounts/telegram/auth/state with body:{}", result);
        return result;
    }

    @Operation(summary = "Logout from telegram account")
    @PostMapping("/telegram/auth/logout")
    public void telegramAccountLogout(
            @RequestParam
            final String accountId) {
        log.info("Received request POST /accounts/telegram/auth/logout with accountId:{}", accountId);
        accountService.telegramAccountLogout(accountId);
        log.info("Produced response 200 for POST /accounts/telegram/auth/logout");
    }

    @Operation(summary = "Add telegram credentials")
    @PostMapping("/telegram/credentials/add")
    public void addTelegramCredentials(
            @RequestBody
            final TelegramCredentialsCreateDto createDto) {
        log.info("Received request POST /accounts/telegram/credentials/add");
        accountService.addTelegramCredentials(createDto);
        log.info("Produced response 200 for POST /accounts/telegram/credentials/add");
    }

    @Operation(summary = "Get gmail auth url")
    @PostMapping(value = "/gmail/auth", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String getGmailAuthUrl(
            @RequestPart
            final MultipartFile file) {
        log.info("Received request POST /accounts/gmail/auth");
        final String result = accountService.getGmailAuthUrl(file);
        log.info("Produced response 200 for POST /accounts/gmail/auth with body:{}", result);
        return result;
    }

    @Operation(summary = "Get connected gmail accounts")
    @GetMapping("/gmail/accounts")
    public List<GmailAccountInfoDto> getConnectedGmailAccounts() {
        log.info("Received request GET /accounts/gmail/accounts");
        final List<GmailAccountInfoDto> result = accountService.getConnectedGmailAccounts();
        log.info("Produced response 200 for GET /accounts/gmail/accounts with body:{}", result);
        return result;
    }

    @Operation(summary = "Get connected discord bots")
    @GetMapping("/discord/bots")
    public List<DiscordBotInfoDto> getConnectedDiscordBots() {
        log.info("Received request GET /accounts/discord/bots");
        final List<DiscordBotInfoDto> result = accountService.getConnectedDiscordBots();
        log.info("Produced response 200 for GET /accounts/discord/bots with body:{}", result);
        return result;
    }

    @Operation(summary = "Add discord bot")
    @PostMapping("/discord/bot")
    public DiscordBotInfoDto addDiscordBot(
            @RequestBody
            final AddBotRequestDto request) {
        log.info("Received request POST /accounts/discord/bot/add");
        final DiscordBotInfoDto result = accountService.addDiscordBot(request);
        log.info("Produced response 200 for POST /accounts/discord/bot/add with body:{}", result);
        return result;
    }

    @Operation(summary = "Register")
    @PostMapping("/users/register")
    public UserDto register(
            @RequestBody
            final UserCreateDto createDto) {
        log.info("Received request POST /accounts/users/register");
        final UserDto result = accountService.register(createDto);
        log.info("Produced response 200 for POST /accounts/users/register with body:{}", result);
        return result;
    }

    @Operation(summary = "Login")
    @PostMapping("/users/login")
    public Map<String, String> login(
            @RequestBody
            final AuthRequestDto request) {
        log.info("Received request POST /accounts/users/login");
        final Map<String, String> result = accountService.login(request);
        log.info("Produced response 200 for POST /accounts/users/login with body:{}", result);
        return result;
    }

    @Operation(summary = "Refresh token")
    @PostMapping("/users/refresh")
    public Map<String, String> refresh(
            @RequestParam
            final String refreshToken) {
        log.info("Received request POST /accounts/users/refresh");
        final Map<String, String> result = accountService.refresh(refreshToken);
        log.info("Produced response 200 for POST /accounts/users/refresh with body:{}", result);
        return result;
    }

    @Operation(summary = "Patch user")
    @PatchMapping("/users/{id}")
    public UserDto patchUser(
            @PathVariable
            final Long id,
            @RequestBody
            final UserPatchDto patch) {
        log.info("Received request PATCH /accounts/users/{}", id);
        final UserDto result = accountService.patchUser(id, patch);
        log.info("Produced response 200 for PATCH /accounts/users/{} with body:{}", id, result);
        return result;
    }

    @Operation(summary = "Get self user info")
    @GetMapping("/users/me")
    public UserDto getMe() {
        log.info("Received request GET /accounts/users/me");
        final UserDto result = accountService.getMe();
        log.info("Produced response 200 for GET /accounts/users/me with body:{}", result);
        return result;
    }

    @Operation(summary = "Add contact profile")
    @PostMapping("/ai/contact-profile/add")
    public ContactProfileDto addContactProfile(
            @RequestBody
            final ContactProfileCreateDto createDto) {
        log.info("Received request POST /accounts/ai/contact-profile/add");
        final ContactProfileDto result = accountService.addContactProfile(createDto);
        log.info("Produced response 200 for POST /accounts/ai/contact-profile/add with body:{}", result);
        return result;
    }

    @Operation(summary = "Patch contact profile")
    @PatchMapping("/ai/contact-profile/patch/{id}")
    public ContactProfileDto patchProfile(
            @PathVariable
            final Long id,
            @RequestBody
            final ContactProfilePatchDto patch) {
        log.info("Received request PATCH /accounts/ai/contact-profile/patch/{}", id);
        final ContactProfileDto result = accountService.patchProfile(id, patch);
        log.info("Produced response 200 for PATCH /accounts/ai/contact-profile/patch/{}", id, result);
        return result;
    }
}
