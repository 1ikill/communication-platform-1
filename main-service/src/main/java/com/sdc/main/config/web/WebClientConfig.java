package com.sdc.main.config.web;

import com.sdc.main.config.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient configuration.
 * @since 11.2025
 */
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final CurrentUser currentUser;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter(authorizationFilter())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    private ExchangeFilterFunction authorizationFilter() {
        return (request, next) -> {

            final String token = currentUser.getToken();
            if (token == null) {
                return next.exchange(request);
            }

            ClientRequest newRequest = ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .build();

            return next.exchange(newRequest);
        };
    }
}
