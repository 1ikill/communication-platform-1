package com.sdc.main.service;

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
import com.sdc.main.integration.client.AIServiceClient;
import com.sdc.main.integration.client.DiscordServiceClient;
import com.sdc.main.integration.client.GmailServiceClient;
import com.sdc.main.integration.client.TelegramServiceClient;
import com.sdc.main.integration.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Account-management service.
 * @since 11.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final TelegramServiceClient telegramClient;
    private final AIServiceClient aiClient;
    private final GmailServiceClient gmailClient;
    private final DiscordServiceClient discordClient;
    private final UserServiceClient userClient;

    public UserTdlibDto getTelegramAccountInfo(final String accountId) {
        return telegramClient.getAccountInfo(accountId);
    }

    public List<TelegramAccountDto> getConnectedTelegramAccounts(){
        return telegramClient.getAllAccountsInfo();
    }

    public void submitTelegramPhoneNumber(final String phone, final String accountId) {
        telegramClient.sendPhoneNumber(phone, accountId);
    }

    public void submitTelegramCode(final String code, final String accountId) {
        telegramClient.sendCode(code, accountId);
    }

    public void submitTelegramPassword(final String password, final String accountId) {
        telegramClient.sendPassword(password, accountId);
    }

    public AuthorizationStateTdlib getTelegramAccountAuthState(final String accountId) {
        return telegramClient.getAuthorizationState(accountId);
    }

    public void telegramAccountLogout(final String accountId) {
        telegramClient.logout(accountId);
    }

    public void addTelegramCredentials(final TelegramCredentialsCreateDto createDto) {
        telegramClient.addCredentials(createDto);
    }

    public String getGmailAuthUrl(final MultipartFile file) {
        return gmailClient.getAuthUrl(file);
    }

    public List<GmailAccountInfoDto> getConnectedGmailAccounts() {
        return gmailClient.getMe();
    }

    public List<DiscordBotInfoDto> getConnectedDiscordBots() {
        return discordClient.getConnectedBots();
    }

    public DiscordBotInfoDto addDiscordBot(final AddBotRequestDto request) {
        return discordClient.addBot(request);
    }

    public UserDto register(final UserCreateDto createDto) {
        return userClient.register(createDto);
    }

    public Map<String, String> login(final AuthRequestDto request) {
        return userClient.login(request);
    }

    public Map<String, String> refresh(final String refreshToken) {
        return userClient.refresh(refreshToken);
    }

    public UserDto patchUser(final Long id, final UserPatchDto patch) {
        return userClient.patchUser(id, patch);
    }

    public UserDto getMe() {
        return userClient.getMe();
    }

    public ContactProfileDto addContactProfile(final ContactProfileCreateDto createDto) {
        return aiClient.addContactProfile(createDto);
    }

    public ContactProfileDto patchProfile(final Long id, final ContactProfilePatchDto patch) {
        return aiClient.pathProfile(id, patch);
    }

}
