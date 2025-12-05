package com.sdc.main.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.CommunicationPlatformType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Abstract message request DTO.
 * @since 11.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "platform",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TelegramMessageRequestDto.class, name = CommunicationPlatformType.TELEGRAM_NAME),
        @JsonSubTypes.Type(value = ViberMessageRequestDto.class, name = CommunicationPlatformType.VIBER_NAME),
        @JsonSubTypes.Type(value = GmailMessageRequestDto.class, name = CommunicationPlatformType.EMAIL_NAME),
})
@NoArgsConstructor
@Schema(description = "Message Request DTO")
public abstract class MessageRequestDto {
    @Schema(description = "Platform")
    private CommunicationPlatformType platform;

    @Schema(description = "Chat identifier")
    private String chatIdentifier;

    @Schema(description = "Message")
    private String message;
}
