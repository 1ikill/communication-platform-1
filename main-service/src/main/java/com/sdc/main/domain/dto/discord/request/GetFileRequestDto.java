package com.sdc.main.domain.dto.discord.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

/**
 * Dto for file request.
 * @since 12.2025
 */
@Schema(description = "Get file request DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFileRequestDto {
    @Schema(description = "Request url")
    @NotBlank(message = "Url must no be blank")
    @URL
    private String url;
}
