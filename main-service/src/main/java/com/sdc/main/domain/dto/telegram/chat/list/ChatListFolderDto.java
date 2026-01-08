package com.sdc.main.domain.dto.telegram.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Chat list folder telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description ="Folder DTO")
public class ChatListFolderDto extends ChatListTdlib {
    @Schema(description = "Folder id")
    private Integer chatFolderId;
}
