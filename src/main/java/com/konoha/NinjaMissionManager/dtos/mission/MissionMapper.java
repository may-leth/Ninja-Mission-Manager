package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Status;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring", uses = NinjaMapper.class)
public interface MissionMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "status", ignore = true),
            @Mapping(target = "assignedNinjas", ignore = true)
    })
    Mission dtoToEntity(MissionCreateRequest dto);

    @Mappings({
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "id", ignore = true)
    })
    void updateEntityFromDto(MissionUpdateRequest request, @MappingTarget Mission mission);

    @Mapping(target = "assignedNinjas", source = "assignedNinjas")
    MissionResponse entityToDto(Mission mission);

    MissionSummaryResponse entityToSummaryDto(Mission mission);
}