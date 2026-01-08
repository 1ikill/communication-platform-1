package com.sdc.main.domain.dto.telegram.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.main.domain.constants.telegram.TelegramUserType;
import lombok.Data;

/**
 * Base class for Telegram user type DTOs
 * @since 12.2025
 */
@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserTypeRegularDto.class, name = TelegramUserType.REGULAR_NAME),
        @JsonSubTypes.Type(value = UserTypeDeletedDto.class, name = TelegramUserType.DELETED_NAME),
        @JsonSubTypes.Type(value = UserTypeUnknownDto.class, name = TelegramUserType.UNKNOWN_NAME),
        @JsonSubTypes.Type(value = UserTypeBotDto.class, name = TelegramUserType.BOT_NAME)
})
public abstract class UserTypeTdlib {
}
