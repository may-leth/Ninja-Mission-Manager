package com.may.Ninja.Mission.Manager.dtos;

import com.may.Ninja.Mission.Manager.models.Mission;

public interface MissionMapper {
    static Mission dtoToEntity(MissionRequest dto){
        return Mission.builder()
                .name(dto.name())
                .rank(dto.rank())
                .assignedTo(dto.assignedTo())
                .completed(dto.completed() != null && dto.completed())
                .build();
    }

    static MissionResponse entityToDto(Mission mission){
        return new MissionResponse(
                mission.getId(),
                mission.getName(),
                mission.getRank(),
                mission.getAssignedTo(),
                mission.isCompleted()
        );
    }
}
