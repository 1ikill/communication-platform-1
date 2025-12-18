package com.sdc.telegram.config;

import com.sdc.telegram.config.properties.TelegramConfigurationProperties;
import com.sdc.telegram.domain.model.TelegramCredentials;
import com.sdc.telegram.repository.TelegramCredentialsRepository;
import com.sdc.telegram.utils.CryptoUtils;
import com.sdc.telegram.utils.ResultHandlerImpl;
import lombok.extern.slf4j.Slf4j;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TDLib client manager.
 * @since 11.2025
 */
@Slf4j
@Component
public class TelegramClientManager  {
    private final Map<String, Client> clients = new HashMap<>();
    private final Map<Client, String> clientToAccountMap = new HashMap<>();
    private final TelegramConfigurationProperties configuration;
    private final ResultHandlerImpl resultHandler;
    private final TelegramCredentialsRepository credentialsRepository;
    private final CryptoUtils cryptoUtils;

    public TelegramClientManager(TelegramConfigurationProperties configuration, ResultHandlerImpl resultHandler,
                                 TelegramCredentialsRepository credentialsRepository, CryptoUtils cryptoUtils) throws Exception {
        this.configuration = configuration;
        this.resultHandler = resultHandler;
        this.credentialsRepository = credentialsRepository;
        this.cryptoUtils = cryptoUtils;
        preloadLibraries();
        initializeClients();
    }

    /**
     * Preload required libraries.
     */
    private void preloadLibraries() {
        try {
            String os = System.getProperty("os.name");
            if (os != null && os.toLowerCase().startsWith("windows")) {
                System.loadLibrary("libcrypto-3-x64");
                System.loadLibrary("libssl-3-x64");
                System.loadLibrary("zlib1");
            }
            System.loadLibrary("tdjni");
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize TDLib clients.
     */
    private void initializeClients() {
        final List<TelegramCredentials> telegramCredentials = credentialsRepository.findAll();
        if (telegramCredentials.isEmpty()){
            return;
        }

        for (TelegramCredentials telegramCredential : telegramCredentials) {
            Client client = Client.create(resultHandler, null, null);
            resultHandler.setClient(client);
            clients.put(telegramCredential.getAccountId(), client);
            clientToAccountMap.put(client, telegramCredential.getAccountId());
            try {
                Client.execute(new TdApi.SetLogVerbosityLevel(2));
            } catch (Client.ExecutionException e) {
                throw new RuntimeException("Can't set log verbosity level");
            }
            clients.put(telegramCredential.getAccountId(), client);

            try { 
                initializeClient(client, telegramCredential);
            } catch (Exception e) {
                log.error("Failed to initialize client for account:{}", telegramCredential.getAccountId(), e);
            }
        }
    }

    /**
     * Initialize TDLib client.
     * @param client client for init.
     * @param credentials telegram credentials.
     */
    public void initializeClient(Client client, final TelegramCredentials credentials) throws Exception {
        TdApi.SetTdlibParameters parameters = new TdApi.SetTdlibParameters();
        parameters.apiId = Integer.parseInt(cryptoUtils.decrypt(credentials.getApiId()));
        parameters.apiHash = cryptoUtils.decrypt(credentials.getApiHash());
        parameters.systemLanguageCode = configuration.getSystemLanguageCode();
        parameters.deviceModel = configuration.getDeviceModel();
        parameters.applicationVersion = configuration.getApplicationVersion();
        parameters.useMessageDatabase = configuration.getUseMessageDatabase();
        parameters.useSecretChats = configuration.getUseSecretChats();
        parameters.databaseDirectory = configuration.getDatabaseDirectory() + credentials.getAccountId();

        client.send(parameters, result -> {
            if (result instanceof TdApi.Ok) {
                log.info("Client initialized for account:{} ", credentials.getAccountId());
            } else {
                log.error("Failed to initialize client for account:{}", credentials.getAccountId());
            }
        });

        client.send(new TdApi.GetAuthorizationState(), resultHandler);
    }

    public Client getClient(String accountId) {
        return clients.get(accountId);
    }

    public String getAccountIdForClient(Client client) {
        return clientToAccountMap.get(client);
    }

    public void putClient(final Client client, final String accountId) {
        clientToAccountMap.put(client, accountId);
    }

    public void putAccountId(final String accountId, final Client client) {
        clients.put(accountId, client);
    }

}