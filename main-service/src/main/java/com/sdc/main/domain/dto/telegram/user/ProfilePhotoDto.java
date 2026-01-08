package com.sdc.main.domain.dto.telegram.user;

import com.sdc.main.domain.dto.telegram.message.content.FileDto;
import com.sdc.main.domain.dto.telegram.message.content.MinithumbnailDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Profile photo DTO")
/**
 * DTO representing a user profile photo
 * @since 12.2025
 */
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
