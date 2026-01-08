package com.sdc.main.domain.dto.telegram.message.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramContentType;
import lombok.Getter;
import lombok.Setter;

/**
 * Message content telegram abstract class.
 * @since 12.2025
 */
@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MessagePhotoDto.class, name = TelegramContentType.IMAGE_NAME),
        @JsonSubTypes.Type(value = MessageTextDto.class, name = TelegramContentType.TEXT_NAME),
        @JsonSubTypes.Type(value = MessageDocumentDto.class, name = TelegramContentType.DOCUMENT_NAME),
        @JsonSubTypes.Type(value = MessageVideoDto.class, name = TelegramContentType.VIDEO_NAME)
})
public abstract class MessageContentTdlib {
    private TelegramContentType type;
}

