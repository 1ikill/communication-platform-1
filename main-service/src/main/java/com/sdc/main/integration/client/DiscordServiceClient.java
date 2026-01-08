package com.sdc.main.integration.client;

import com.sdc.main.config.properties.MicroserviceIntegrationProperties;
import com.sdc.main.domain.dto.discord.bot.DiscordBotInfoDto;
import com.sdc.main.domain.dto.discord.chat.DiscordPrivateChatDto;
import com.sdc.main.domain.dto.discord.guild.GuildChannelDto;
import com.sdc.main.domain.dto.discord.guild.GuildDto;
import com.sdc.main.domain.dto.discord.guild.GuildUserDto;
import com.sdc.main.domain.dto.discord.message.ChannelMessageDto;
import com.sdc.main.domain.dto.discord.message.DiscordPrivateMessageDto;
import com.sdc.main.domain.dto.discord.request.AddBotRequestDto;
import com.sdc.main.domain.dto.discord.request.GetFileRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

/**
 * WebClient for discord-service.
 * @since 11.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordServiceClient {
    private static final String USER_ID_PARAM = "userId";
    private static final String CHANNEL_ID_PARAM = "channelId";
    private static final String MESSAGE_PARAM = "message";
    private static final String QUERY_PARAM = "query";
    private static final String LIMIT_PARAM = "limit";
    private static final String MESSAGE_IDS_PARAM = "messageIds";
    private static final String UPDATED_MESSAGE_PARAM = "updatedMessage";

    private final WebClient webClient;
    private final MicroserviceIntegrationProperties properties;

    public List<DiscordBotInfoDto> getConnectedBots() {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getGetBotsUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DiscordBotInfoDto>>() {
                })
                .block();
    }

    public DiscordBotInfoDto addBot(final AddBotRequestDto request) {
        return webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getAddBotUrl())
                        .build()
                        .toString())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<DiscordBotInfoDto>() {
                })
                .block();
    }

    public List<DiscordPrivateChatDto> getPrivateChats(final Long botId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + properties.getDiscordService().getGetPrivateChatsUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DiscordPrivateChatDto>>() {
                })
                .block();
    }

    public List<GuildDto> getGuilds(final Long botId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + properties.getDiscordService().getGetGuildsUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GuildDto>>() {
                })
                .block();
    }

    public List<GuildChannelDto> getChannels(final Long botId, final Long guildId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + "/chats/guilds/" + guildId + "/channels")
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GuildChannelDto>>() {
                })
                .block();
    }

    public List<GuildUserDto> getUsers(final Long botId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + properties.getDiscordService().getGetUsersUrl())
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GuildUserDto>>() {
                })
                .block();
    }

    public List<DiscordPrivateChatDto> searchChats(final Long botId, final String query) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + properties.getDiscordService().getSearchChatsUrl())
                        .queryParam(QUERY_PARAM, query)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DiscordPrivateChatDto>>() {
                })
                .block();
    }

    public List<GuildChannelDto> searchGuildChannels(final Long botId, final Long guildId, final String query) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + "/chats/" + guildId + "/channels/search")
                        .queryParam(QUERY_PARAM, query)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GuildChannelDto>>() {
                })
                .block();
    }

    public DiscordPrivateChatDto getChat(final Long botId, final String channelId) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path(botId + "/" + channelId)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<DiscordPrivateChatDto>() {
                })
                .block();
    }

    public StreamingResponseBody getImage(final GetFileRequestDto request) {
        return outputStream -> {
            webClient.post()
                    .uri(fromHttpUrl(properties.getDiscordService().getGetImageUrl())
                            .build()
                            .toString())
                    .bodyValue(request)
                    .accept(MediaType.IMAGE_JPEG)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming image", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

    public StreamingResponseBody getVideo(final GetFileRequestDto request) {
        return outputStream -> {
            webClient.post()
                    .uri(fromHttpUrl(properties.getDiscordService().getGetVideoUrl())
                            .build()
                            .toString())
                    .bodyValue(request)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming video", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

    public StreamingResponseBody getDocument(final GetFileRequestDto request) {
        return outputStream -> {
            webClient.post()
                    .uri(fromHttpUrl(properties.getDiscordService().getGetDocumentUrl())
                            .build()
                            .toString())
                    .bodyValue(request)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class)
                    .map(dataBuffer -> {
                        try {
                            byte[] bytes = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(bytes);
                            outputStream.write(bytes);
                            return dataBuffer;
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        } finally {
                            DataBufferUtils.release(dataBuffer);
                        }
                    })
                    .doOnError(e -> {
                        try {
                            log.error("Error streaming document", e);
                            outputStream.close();
                        } catch (IOException ex) {
                            log.error("Error closing stream", ex);
                        }
                    })
                    .blockLast();
        };
    }

    public void sendDirectMessage(final String chatId, final String messageText, final Long accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("messages/" + accountId + properties.getDiscordService().getSendDirectMessageUrl())
                        .queryParam(USER_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, messageText)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendChannelMessage(final String chatId, final String messageText, final Long accountId) {
        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("messages/" + accountId + properties.getDiscordService().getSendChannelMessageUrl())
                        .queryParam(CHANNEL_ID_PARAM, chatId)
                        .queryParam(MESSAGE_PARAM, messageText)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void sendDmFileMessage(final Long botId, final String userId, final List<MultipartFile> files, final String message) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (MultipartFile file : files) {
            builder.part("files", file.getResource());
        }

        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + properties.getDiscordService().getSendPrivateFileMessageUrl())
                        .queryParam(USER_ID_PARAM, userId)
                        .queryParam(MESSAGE_PARAM, message)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();

    }

    public void sendChannelFileMessage(final Long botId, final String channelId, final List<MultipartFile> files, final String message) {
        final MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (MultipartFile file : files) {
            builder.part("files", file.getResource());
        }

        webClient.post()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + properties.getDiscordService().getSendChannelFileMessageUrl())
                        .queryParam(CHANNEL_ID_PARAM, channelId)
                        .queryParam(MESSAGE_PARAM, message)
                        .build()
                        .toString())
                .contentType(MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public List<ChannelMessageDto> getGuildChannelHistory(final Long botId, final String channelId, final int limit) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/channels/" + channelId + "/history")
                        .queryParam(LIMIT_PARAM, limit)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChannelMessageDto>>() {
                })
                .block();
    }

    public List<DiscordPrivateMessageDto> getHistory(final Long botId, final String channelId, final int limit) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/dm/" + channelId + "/history")
                        .queryParam(LIMIT_PARAM, limit)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DiscordPrivateMessageDto>>() {
                })
                .block();
    }

    public List<DiscordPrivateMessageDto> searchPrivateMessages(final Long botId, final String channelId, final String query) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/dm/" + channelId + "/search")
                        .queryParam(QUERY_PARAM, query)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<DiscordPrivateMessageDto>>() {
                })
                .block();
    }

    public List<ChannelMessageDto> searchGuildMessages(final Long botId, final String channelId, final String query) {
        return webClient.get()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/channel/" + channelId + "/search")
                        .queryParam(QUERY_PARAM, query)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ChannelMessageDto>>() {
                })
                .block();
    }

    public void deletePrivateMessage(final Long botId, final List<Long> messageIds) {
        webClient.delete()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + properties.getDiscordService().getDeletePrivateMessageUrl())
                        .queryParam(MESSAGE_IDS_PARAM, messageIds)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void deleteGuildMessage(final Long botId, final List<String> messageIds, final String channelId) {
        webClient.delete()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + properties.getDiscordService().getDeleteGuildMessageUrl())
                        .queryParam(CHANNEL_ID_PARAM, channelId)
                        .queryParam(MESSAGE_IDS_PARAM, messageIds)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    public void updateMessage(final Long botId, final Long messageId, final String updatedMessage) {
        webClient.put()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/update/" + messageId)
                        .queryParam(UPDATED_MESSAGE_PARAM, updatedMessage)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

    }

    public void updateGuildMessage(final Long botId, final String channelId, final String messageId, final String updatedMessage) {
        webClient.put()
                .uri(fromHttpUrl(properties.getDiscordService().getBaseUrl()).path("/messages/" + botId + "/update/channel/" + messageId)
                        .queryParam(CHANNEL_ID_PARAM, channelId)
                        .queryParam(UPDATED_MESSAGE_PARAM, updatedMessage)
                        .build()
                        .toString())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}
