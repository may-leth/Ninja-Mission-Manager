package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import com.konoha.NinjaMissionManager.models.Village;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NinjaMapperImpl implements NinjaMapper{
    private final VillageMapper villageMapper;
    private final MissionMapper missionMapper;

    public NinjaMapperImpl(VillageMapper villageMapper, MissionMapper missionMapper){
        this.villageMapper = villageMapper;
        this.missionMapper = missionMapper;
    }

    @Override
    public Ninja dtoToEntity(NinjaRegisterRequest dto, Village village, Rank rank, Integer missionsCompletedCount, Boolean isAnbu, Set<Role> roles) {
        return Ninja.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .rank(rank)
                .village(village)
                .missionsCompletedCount(missionsCompletedCount)
                .isAnbu(isAnbu)
                .roles(roles)
                .build();
    }

    @Override
    public Ninja dtoToEntity(KageCreateNinjaRequest dto, Village village) {
        return Ninja.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .rank(dto.rank())
                .village(village)
                .missionsCompletedCount(0)
                .isAnbu(dto.isAnbu())
                .roles(dto.roles())
                .build();
    }

    @Override
    public NinjaResponse entityToDto(Ninja ninja) {
        VillageResponse village = villageMapper.entityToDto(ninja.getVillage());
        Set<MissionSummaryResponse> assignedMission = ninja.getAssignedMissions().stream()
                .map(missionMapper::entityToSummaryDto)
                .collect(Collectors.toSet());
        return new NinjaResponse(
                ninja.getId(),
                ninja.getName(),
                ninja.getEmail(),
                ninja.getRank().name(),
                village,
                ninja.getMissionsCompletedCount(),
                ninja.isAnbu(),
                assignedMission
        );
    }

    @Override
    public NinjaSummaryResponse entityToSummaryDto(Ninja ninja) {
        return new NinjaSummaryResponse(
                ninja.getId(),
                ninja.getName()
        );
    }
}