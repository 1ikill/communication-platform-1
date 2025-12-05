package com.sdc.telegram.domain.dto.tdlib.user;

import com.sdc.telegram.domain.dto.tdlib.message.content.FileDto;
import com.sdc.telegram.domain.dto.tdlib.message.content.MinithumbnailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Profile photo DTO")
public class ProfilePhotoDto {
    @Schema(description = "Id")
    private Long id;

    @Schema(description = "Small")
    private FileDto small;

    @Schema(description = "Big")
    private FileDto big;

    @Schema(description = "Minithumbnail")
    private MinithumbnailDto minithumbnail;

    @Schema(description = "Has animation")
    private Boolean hasAnimation;

    @Schema(description = "Is personal")
    private Boolean isPersonal;
}
