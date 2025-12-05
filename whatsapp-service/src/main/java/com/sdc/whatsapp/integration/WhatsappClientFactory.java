package com.sdc.whatsapp.integration;

import com.sdc.whatsapp.domain.model.WhatsappCredentials;

import com.sdc.whatsapp.utils.CryptoUtils;
import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.configuration.ApiVersion;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import com.whatsapp.api.impl.WhatsappBusinessManagementApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsappClientFactory {
    private final CryptoUtils cryptoUtils;

    public WhatsappBusinessCloudApi createCloudClient(WhatsappCredentials account) throws Exception {
        String token = cryptoUtils.decrypt(account.getAccessToken());
        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(token);
        return factory.newBusinessCloudApi(ApiVersion.V19_0);
    }

    public WhatsappBusinessManagementApi createManagementClient(WhatsappCredentials account) throws Exception {
        String token = cryptoUtils.decrypt(account.getAccessToken());
        WhatsappApiFactory factory = WhatsappApiFactory.newInstance(token);
        return factory.newBusinessManagementApi();
    }
}
