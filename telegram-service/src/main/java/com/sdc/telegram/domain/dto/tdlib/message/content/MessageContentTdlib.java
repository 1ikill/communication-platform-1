package com.sdc.telegram.domain.dto.tdlib.message.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramContentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
public abstract class MessageContentTdlib {
    private TelegramContentType type;
}

