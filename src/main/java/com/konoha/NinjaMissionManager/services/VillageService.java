package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Village;
import com.konoha.NinjaMissionManager.repositories.VillageRepository;
import com.konoha.NinjaMissionManager.specifications.VillageSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VillageService {
    private final VillageRepository villageRepository;
    private final VillageMapper villageMapper;

    public List<VillageResponse> getAllVillages(Optional<String> kageName){
        Specification<Village> specification = VillageSpecificationBuilder.builder()
                .kageName(kageName)
                .build();

        List<Village> villages = villageRepository.findAll(specification);

        return villages.stream()
                .map(villageMapper::entityToDto)
                .collect(Collectors.toList());
    }

    public VillageResponse getVillageResponseById(Long id){
        Village village = villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with ID: " + id));
        return villageMapper.entityToDto(village);
    }

    public Village getVillageEntityById(Long id){
        return villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with ID: " + id));
    }
}
