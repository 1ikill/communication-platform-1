package com.sdc.telegram.domain.mapper.user;

import com.sdc.telegram.domain.dto.tdlib.user.status.VerificationStatusDto;
import org.drinkless.tdlib.TdApi;
import org.mapstruct.Mapper;

@Mapper
public abstract class VerificationStatusMapper {
    public abstract VerificationStatusDto toDto(final TdApi.VerificationStatus source);
}
