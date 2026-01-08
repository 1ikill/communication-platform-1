package com.sdc.main.domain.dto.telegram;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Telegram chat folder DTO")
public class TelegramChatFolderDto {
    @Schema(description = "Folder name")
    private String folderName;

    @Schema(description = "Folder Id")
    private int folderId;
}
