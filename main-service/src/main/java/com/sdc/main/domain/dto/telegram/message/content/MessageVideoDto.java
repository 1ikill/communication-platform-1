package com.sdc.main.domain.dto.telegram.message.content;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message video telegram DTO.
 * @since 12.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Message Video DTO")
public class MessageVideoDto extends MessageContentTdlib {
    @Schema(description = "Video")
    private VideoDto video;

    @Schema(description = "Alternative videos")
    private AlternativeVideoDto[] alternativeVideos;

    @Schema(description = "Caption")
    private FormattedTextDto caption;

    @Schema(description = "Show caption above media")
    private Boolean showCaptionAboveMedia;

    @Schema(description = "Has spoiler")
    private Boolean hasSpoiler;

    @Schema(description = "Is secret")
    private Boolean isSecret;
}
