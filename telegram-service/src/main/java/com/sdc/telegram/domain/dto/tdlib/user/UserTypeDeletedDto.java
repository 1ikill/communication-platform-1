package com.sdc.telegram.domain.dto.tdlib.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "User type deleted DTO")
/**
 * DTO representing a deleted user
 * @since 12.2025
 */
public class UserTypeDeletedDto extends UserTypeTdlib {

}
