package com.sdc.telegram.domain.dto.tdlib.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Usernames DTO")
public class UsernamesDto {
    @Schema(description = "Active usernames")
    private List<String> activeUsernames;

    @Schema(description = "Disabled usernames")
    private List<String> disabledUsernames;

    @Schema(description = "Editable username")
    private String editableUsername;
}
