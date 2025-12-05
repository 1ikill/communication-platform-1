package com.sdc.telegram.domain.mapper;

import com.sdc.telegram.domain.dto.TelegramCredentialsCreateDto;
import com.sdc.telegram.domain.model.TelegramCredentials;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public abstract class TelegramCredentialsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "apiId", ignore = true)
    @Mapping(target = "apiHash", ignore = true)
    @Mapping(target = "userId", ignore = true)
    public abstract TelegramCredentials fromCreateDto(final TelegramCredentialsCreateDto createDto);
}
