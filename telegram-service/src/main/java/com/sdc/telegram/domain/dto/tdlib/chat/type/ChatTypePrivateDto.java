package com.sdc.telegram.domain.dto.tdlib.chat.type;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Private chat DTO")
public class ChatTypePrivateDto extends ChatTypeTdlib {
    @Schema(description = "User id")
    private Long userId;
}