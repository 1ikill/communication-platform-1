package com.sdc.telegram.utils;

import com.sdc.telegram.config.TelegramClientManager;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

/**
 * Implementation of TDLib result handler for processing Telegram client updates
 * @since 12.2025
 */
@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class ResultHandlerImpl implements Client.ResultHandler {
    private TelegramClientManager clientManager;
    private Client client;

    @Override
    public void onResult(TdApi.Object update) {
        if (update instanceof TdApi.UpdateAuthorizationState authStateUpdate) {
            String accountId = clientManager.getAccountIdForClient(client);
            TdApi.AuthorizationState state = authStateUpdate.authorizationState;

            if (state instanceof TdApi.AuthorizationStateWaitPhoneNumber) {
                log.info("Account {}: Waiting for phone number", accountId);
            } else if (state instanceof TdApi.AuthorizationStateWaitCode) {
                log.info("Account {}: Waiting for authentication code", accountId);
            } else if (state instanceof TdApi.AuthorizationStateWaitPassword) {
                log.info("Account {}: Waiting for 2FA password", accountId);
            } else if (state instanceof TdApi.AuthorizationStateReady) {
                log.info("Account {}: Auth successful", accountId);
            }
        }
    }
}
