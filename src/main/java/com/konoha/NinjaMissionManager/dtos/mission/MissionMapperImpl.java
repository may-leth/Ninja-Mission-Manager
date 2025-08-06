package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaSummaryResponse;
import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MissionMapperImpl implements MissionMapper{
    private final NinjaMapper ninjaMapper;

    public MissionMapperImpl(NinjaMapper ninjaMapper){
        this.ninjaMapper = ninjaMapper;
    }

    @Override
    public Mission dtoToEntity(MissionRequest dto, Status status, LocalDateTime creationDate, Set<Ninja> assignedNinjas) {
        return Mission.builder()
                .title(dto.title())
                .description(dto.description())
                .reward(dto.reward())
                .difficulty(dto.difficulty())
                .status(status)
                .creationDate(creationDate)
                .assignedNinjas(assignedNinjas)
                .build();
    }

    @Override
    public MissionResponse entityToDto(Mission mission) {
        Set<NinjaSummaryResponse> ninjas = mission.getAssignedNinjas().stream()
                .map(ninjaMapper::entityToSummaryDto)
                .collect(Collectors.toSet());
        return new MissionResponse(
                mission.getId(),
                mission.getTitle(),
                mission.getDescription(),
                mission.getReward(),
                mission.getDifficulty(),
                mission.getStatus(),
                mission.getCreationDate(),
                ninjas
        );
    }

    @Override
    public MissionSummaryResponse entityToSummaryDto(Mission mission) {
        return new MissionSummaryResponse(
                mission.getId(),
                mission.getTitle(),
                mission.getDifficulty(),
                mission.getStatus()
        );
    }
}