package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.domain.dto.tdlib.auth.AuthorizationStateTdlib;
import com.sdc.telegram.domain.mapper.auth.AuthorizationStateTdlibMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Service for handling Telegram authentication operations
 * @since 12.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthService {
    private final TelegramClientManager clientManager;
    private final AuthorizationStateTdlibMapper authorizationStateTdlibMapper;

    /**
     * Sends a phone number to Telegram for authentication
     *
     * @param phone the phone number to send
     * @param accountId the account identifier
     * @throws IllegalArgumentException if accountId is invalid
     */
    public void sendPhoneNumber(final String phone, final String accountId) {
        final Client client = clientManager.getClient(accountId);
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("Invalid accountId: " + accountId);
        }
        TdApi.GetAuthorizationState getAuthorizationState = new TdApi.GetAuthorizationState();

        client.send(getAuthorizationState, result -> {
            if (result instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
                client.send(new TdApi.SetAuthenticationPhoneNumber(phone, null), null);
                log.info("Sent phone number:{} for account:{}", phone, accountId);
            } else if (result instanceof TdApi.Error) {
                throw new RuntimeException("Error sending phone: " + result);
            }
        });
    }

    /**
     * Sends the authentication code received from Telegram
     *
     * @param code the authentication code
     * @param accountId the account identifier
     */
    public void sendAuthCode(final String code, final String accountId) {
        Client client = clientManager.getClient(accountId);
        TdApi.GetAuthorizationState getAuthorizationState = new TdApi.GetAuthorizationState();

        client.send(getAuthorizationState, result -> {
            if (result instanceof TdApi.AuthorizationStateWaitCode) {
                client.send(new TdApi.CheckAuthenticationCode(code), null);
                log.info("Auth successful for account:{}", accountId);
            } else if (result instanceof TdApi.Error) {
                throw new RuntimeException("Error sending code: "  + result);
            }
        });
    }

    /**
     * Sends the password for two-factor authentication
     *
     * @param password the account password
     * @param accountId the account identifier
     */
    public void sendPassword(final String password, final String accountId) {
        Client client = clientManager.getClient(accountId);
        TdApi.GetAuthorizationState getAuthorizationState = new TdApi.GetAuthorizationState();

        client.send(getAuthorizationState, result -> {
            if (result instanceof TdApi.AuthorizationStateWaitPassword) {
                client.send(new TdApi.CheckAuthenticationPassword(password), null);
                log.info("Auth successful for account:{}", accountId);
            } else if (result instanceof TdApi.Error) {
                throw new RuntimeException("Error sending password: "  + result);
            }
        });
    }

    /**
     * Logs out the specified Telegram account
     *
     * @param accountId the account identifier
     * @throws IllegalArgumentException if accountId is invalid
     */
    public void logout(final String accountId) {
        Client client = clientManager.getClient(accountId);
        if (Objects.isNull(client)) {
            throw new IllegalArgumentException("Invalid accountId: " + accountId);
        }
        client.send(new TdApi.LogOut(), result -> {
            if (result instanceof TdApi.Ok) {
                log.info("Logged out account: {}", accountId);
            }
            else if (result instanceof TdApi.Error) {
                log.error("Error logging out:{}", result);
            }
        });
    }

    /**
     * Retrieves the current authorization state for the specified account
     *
     * @param accountId the account identifier
     * @return the authorization state
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted
     */
    public AuthorizationStateTdlib getAuthorizationState(final String accountId) throws ExecutionException, InterruptedException {
        final Client client = clientManager.getClient(accountId);
        TdApi.GetAuthorizationState getAuthorizationState = new TdApi.GetAuthorizationState();
        final CompletableFuture<TdApi.AuthorizationState> stateFuture = new CompletableFuture<>();
        client.send(getAuthorizationState, result -> {
            if (result instanceof TdApi.Error) {
                throw new RuntimeException("Error getting authorization state for accountId:" + accountId + ", error:" + result);
            }
            else if (result instanceof TdApi.AuthorizationState state){
                stateFuture.complete(state);
            }
        });

        return authorizationStateTdlibMapper.toDto(stateFuture.get());
    }
}
