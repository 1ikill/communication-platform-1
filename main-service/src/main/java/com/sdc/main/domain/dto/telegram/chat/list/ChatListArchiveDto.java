package com.sdc.main.domain.dto.telegram.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat list archive telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@Schema(description = "Archive DTO")
public class ChatListArchiveDto extends ChatListTdlib {
}
