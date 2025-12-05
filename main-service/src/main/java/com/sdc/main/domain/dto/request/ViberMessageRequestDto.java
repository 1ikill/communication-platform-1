package com.sdc.main.domain.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Viber message request DTO")
public class ViberMessageRequestDto extends MessageRequestDto {
    private Long viberField;
}
