package com.konoha.NinjaMissionManager.dtos.village;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VillageMapper {

    @Mappings({
        @Mapping(target = "kage", source = "ninja"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "name", source = "dto.name")
    })
    Village dtoToEntity(VillageRequest dto, Ninja ninja);

    @Mapping(target = "kage", source = "kage.name")
    VillageResponse entityToDto(Village village);
}