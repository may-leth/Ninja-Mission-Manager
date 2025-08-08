package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import com.konoha.NinjaMissionManager.specifications.NinjaSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NinjaServices {
    private final NinjaRepository ninjaRepository;
    private final NinjaMapper ninjaMapper;
    private final MissionMapper missionMapper;

    public List<NinjaResponse> getAllNinjas(Optional<Rank> rank, Optional<Long> villageId, Optional<Boolean> isAnbu){
        Specification<Ninja> specification = NinjaSpecificationBuilder.builder()
                .rank(rank)
                .villageId(villageId)
                .isAnbu(isAnbu)
                .build();

        return ninjaRepository.findAll(specification)
                .stream()
                .map(ninja -> ninjaMapper.entityToDto(ninja, missionMapper))
                .collect(Collectors.toList());
    }

    public NinjaResponse getNinjaById(Long id) {
        Ninja ninja = ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with ID: " + id));
        return ninjaMapper.entityToDto(ninja, missionMapper);
    }
}
