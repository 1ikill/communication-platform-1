package com.sdc.telegram.domain.dto.tdlib.user;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sdc.telegram.domain.constants.TelegramUserType;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public abstract class UserTypeTdlib {
}
