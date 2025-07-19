package com.may.Ninja.Mission.Manager.services;

import com.may.Ninja.Mission.Manager.dtos.MissionMapper;
import com.may.Ninja.Mission.Manager.dtos.MissionResponse;
import com.may.Ninja.Mission.Manager.models.Mission;
import com.may.Ninja.Mission.Manager.repositories.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;

    public List<MissionResponse> getAllMissions(){
        return missionRepository.findAll()
                .stream()
                .map(MissionMapper::entityToDto).
                toList();
    }

    public MissionResponse getMissionById(Long id){
        Mission mission = missionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mission not found with id " + id));
        return MissionMapper.entityToDto(mission);
    }
}
