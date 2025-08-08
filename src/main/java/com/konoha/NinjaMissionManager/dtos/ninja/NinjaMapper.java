package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import com.konoha.NinjaMissionManager.models.Village;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;

@Mapper(componentModel = "spring", uses = {VillageMapper.class})
public interface NinjaMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "assignedMissions", ignore = true),
        @Mapping(source = "dto.name", target = "name")
    })
    Ninja dtoToEntity(NinjaRegisterRequest dto, Village village, Rank rank, Integer missionsCompletedCount, Boolean isAnbu, Set<Role> roles);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "missionsCompletedCount", ignore = true),
        @Mapping(target = "assignedMissions", ignore = true),
        @Mapping(source = "dto.name", target = "name")
    })
    Ninja dtoToEntity(KageCreateNinjaRequest dto, Village village);

    NinjaResponse entityToDto(Ninja ninja, @Context MissionMapper missionMapper);
    NinjaSummaryResponse entityToSummaryDto(Ninja ninja);
}