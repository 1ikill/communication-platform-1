package com.sdc.telegram.service;

import com.sdc.telegram.config.TelegramClientManager;
import com.sdc.telegram.config.security.CurrentUser;
import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.domain.mapper.TelegramCredentialsMapper;
import com.sdc.telegram.repository.TelegramCredentialsRepository;
import com.sdc.telegram.utils.CryptoUtils;
import com.sdc.telegram.utils.ResultHandlerImpl;
import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.Client;
import org.springframework.stereotype.Service;
import com.sdc.telegram.domain.model.TelegramCredentials;

/**
 * Service for {@link TelegramCredentials}
 * @since 11.2025
 */
@Service
@RequiredArgsConstructor
public class TelegramCredentialService {
    private final TelegramCredentialsRepository repository;
    private final TelegramCredentialsMapper mapper;
    private final CryptoUtils cryptoUtils;
    private final CurrentUser currentUser;
    private final ResultHandlerImpl resultHandler;
    private final TelegramClientManager clientManager;

    /**
     * Adds new Telegram credentials for the current user
     * Creates a new client, initializes it with credentials, and saves to the database
     *
     * @param createDto the DTO containing credential information
     * @throws Exception if encryption or initialization fails
     */
    public void addCredentials(final TelegramCredentialsCreateDto createDto) throws Exception {
        final TelegramCredentials credentials = mapper.fromCreateDto(createDto);
        credentials.setUserId(currentUser.getId());
        credentials.setApiId(cryptoUtils.encrypt(createDto.getApiId()));
        credentials.setApiHash(cryptoUtils.encrypt(createDto.getApiHash()));

        Client client = Client.create(resultHandler, null, null);
        clientManager.initializeClient(client, credentials);
        clientManager.putClient(client, createDto.getAccountId());
        clientManager.putAccountId(createDto.getAccountId(), client);
        repository.save(credentials);
    }
}
