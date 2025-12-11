package com.sdc.discord.config;

import com.sdc.gmail.utils.CryptoUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for encryption utils.
 * @since 11.2025
 */
@Configuration
public class CryptoConfig {

    @Value("${credentials.secret-key}")
    private String secretKey;

    @Bean
    public CryptoUtils cryptoUtils() {
        return new CryptoUtils(secretKey);
    }
}