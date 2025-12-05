package com.sdc.user.domain.mapper;

import com.sdc.user.domain.dto.UserCreateDto;
import com.sdc.user.domain.dto.UserDto;
import com.sdc.user.domain.dto.UserPatchDto;
import com.sdc.user.domain.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper
public abstract class UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract User fromDto(final UserDto source);

    public abstract UserDto toDto(final User source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract User fromCreateDto(final UserCreateDto source);

    @Mapping(target = "password", ignore = true)
    public abstract UserPatchDto toPatchDto(final User source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergeDto(@MappingTarget UserPatchDto target, UserPatchDto patch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    public abstract void merge(@MappingTarget User entity, UserPatchDto dto);

}
