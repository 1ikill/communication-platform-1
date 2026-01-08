package com.sdc.main.domain.dto.telegram.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat list main telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@Schema(description = "Main chat list DTO")
public class ChatListMainDto extends ChatListTdlib {
}

