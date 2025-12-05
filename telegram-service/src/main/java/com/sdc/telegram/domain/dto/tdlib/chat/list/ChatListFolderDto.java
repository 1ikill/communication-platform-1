package com.sdc.telegram.domain.dto.tdlib.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description ="Folder DTO")
public class ChatListFolderDto extends ChatListTdlib {
    @Schema(description = "Folder id")
    private Integer chatFolderId;
}
