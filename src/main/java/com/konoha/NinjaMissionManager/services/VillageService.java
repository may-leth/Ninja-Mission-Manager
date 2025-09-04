package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import com.konoha.NinjaMissionManager.repositories.VillageRepository;
import com.konoha.NinjaMissionManager.specifications.VillageSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Village village = getVillageEntityById(id);
        return villageMapper.entityToDto(village);
    }

    public Village getVillageEntityById(Long id){
        return villageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Village not found with ID: " + id));
    }

    @Transactional
    public VillageResponse createVillageInternal(VillageRequest request, Ninja kage){
        Village village = villageMapper.dtoToEntity(request, kage);
        Village savedVillage = villageRepository.save(village);
        return villageMapper.entityToDto(savedVillage);
    }

    @Transactional
    public VillageResponse updateVillageInternal(Village villageToUpdate){
        Village updatedVillage = villageRepository.save(villageToUpdate);
        return villageMapper.entityToDto(updatedVillage);
    }

    @Transactional
    public void deleteVillageInternal(Village villageToDelete){
        villageRepository.delete(villageToDelete);
    }

    public void validateVillageNameNotTaken(String villageName) {
        if (villageRepository.existsByNameIgnoreCase(villageName)) {
            throw new ResourceConflictException("Village with this name already exists: " + villageName);
        }
    }

    public void validateKageAssignment(Long kageId) {
        if (villageRepository.existsByKageId(kageId)) {
            throw new ResourceConflictException("Ninja with ID " + kageId + " is already the Kage of another village.");
        }
    }

    public void validateKageIsNotLeadingAnyVillage(Ninja kage, Long currentVillageId) {
        if (villageRepository.existsByKageAndIdNot(kage, currentVillageId)) {
            throw new ResourceConflictException("Ninja with ID " + kage.getId() + " is already the Kage of another village.");
        }
    }
}