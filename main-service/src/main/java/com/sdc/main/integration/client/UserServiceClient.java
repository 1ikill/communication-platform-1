package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import com.sdc.main.domain.constants.user.RoleType;
import com.sdc.main.domain.dto.user.AuthRequestDto;
import com.sdc.main.domain.dto.user.UserCreateDto;
import com.sdc.main.domain.dto.user.UserDto;
import com.sdc.main.domain.dto.user.UserPatchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for AI-service.
 * @since 11.2025
 */
@Component
@RequiredArgsConstructor
public class UserServiceClient {
    private static final String REFRESH_TOKEN_PARAM = "refreshToken";
    private static final String ROLE_PARAM = "role";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public UserDto register(final UserCreateDto createDto) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getUserService().getRegisterUrl())
                        .build()
                        .toString())
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserDto>() {
                })
                .block();
    }

    public Map<String, String> login(final AuthRequestDto request) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getUserService().getLoginUrl())
                        .build()
                        .toString())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block();
    }

    public Map<String, String> refresh(final String refreshToken) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getUserService().getLoginUrl())
                        .queryParam(REFRESH_TOKEN_PARAM, refreshToken)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .block();
    }

    public UserDto createUserByAdmin(final UserCreateDto createDto, final RoleType role) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getUserService().getCreateUserAdminUrl())
                        .queryParam(ROLE_PARAM, role)
                        .build()
                        .toString())
                .bodyValue(createDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserDto>() {
                })
                .block();
    }

    public UserDto patchUser(final Long id, final UserPatchDto patchDto) {
        return webClient.patch()
                .uri(fromHttpUrl(properties.getUserService().getPatchUserUrl()).path(String.valueOf(id))
                        .build()
                        .toString())
                .bodyValue(patchDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserDto>() {
                })
                .block();
    }

    public UserDto getMe() {
        return webClient.get()
                .uri(fromHttpUrl(properties.getUserService().getGetMeUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<UserDto>() {
                })
                .block();
    }
}
