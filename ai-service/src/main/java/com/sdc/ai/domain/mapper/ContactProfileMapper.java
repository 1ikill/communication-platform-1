package com.sdc.ai.domain.mapper;

import com.sdc.ai.domain.dto.ContactProfileCreateDto;
import com.sdc.ai.domain.dto.ContactProfileDto;
import com.sdc.ai.domain.dto.ContactProfilePatchDto;
import com.sdc.ai.domain.model.ContactProfile;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper for {@link ContactProfile}
 * @since 11.2025
 */
@Mapper
public abstract class ContactProfileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    public abstract ContactProfile fromCreateDto(final ContactProfileCreateDto source);

    public abstract ContactProfileDto toDto(final ContactProfile source);

    public abstract ContactProfilePatchDto toPatchDto(final ContactProfile source);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void mergeDto(@MappingTarget ContactProfilePatchDto target, ContactProfilePatchDto patch);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "chatIdentifier", ignore = true)
    @Mapping(target = "platform", ignore = true)
    @Mapping(target = "userId", ignore = true)
    public abstract void merge(@MappingTarget ContactProfile entity, ContactProfilePatchDto dto);
}
