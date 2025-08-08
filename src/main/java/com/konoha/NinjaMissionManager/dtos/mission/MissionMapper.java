package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Status;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MissionMapper {
    @Mapping(target = "id", ignore = true)
    Mission dtoToEntity(MissionRequest dto, Status status, LocalDateTime creationDate, Set<Ninja> assignedNinjas);

    MissionResponse entityToDto(Mission mission);
    MissionSummaryResponse entityToSummaryDto(Mission mission, @Context NinjaMapper ninjaMapper);
}