package com.sdc.telegram.domain.dto.tdlib.chat.list;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Archive DTO")
public class ChatListArchiveDto extends ChatListTdlib {
}
