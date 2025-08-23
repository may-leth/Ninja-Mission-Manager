package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import com.konoha.NinjaMissionManager.repositories.VillageRepository;
import com.konoha.NinjaMissionManager.specifications.VillageSpecificationBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VillageService {
    private final VillageRepository villageRepository;
    private final VillageMapper villageMapper;
    private final NinjaService ninjaService;

    public VillageService(VillageRepository villageRepository, VillageMapper villageMapper, @Lazy NinjaService ninjaService) {
        this.villageRepository = villageRepository;
        this.villageMapper = villageMapper;
        this.ninjaService = ninjaService;
    }

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
    public VillageResponse createVillage(VillageRequest request){
        Ninja kage = ninjaService.getNinjaEntityById(request.kageId());

        validateVillageNameNotTaken(request.name());
        validateKageAssignment(kage.getId());

        Village village = villageMapper.dtoToEntity(request, kage);
        Village savedVillage = villageRepository.save(village);
        return villageMapper.entityToDto(savedVillage);
    }

    @Transactional
    public VillageResponse updateVillage(Long id, VillageUpdateRequest request){
        Village existingVillage = getVillageEntityById(id);

        if (request.name() != null && !request.name().equalsIgnoreCase(existingVillage.getName())){
            validateVillageNameNotTaken(request.name());
            existingVillage.setName(request.name());
        }

        if (request.kageId() != null){
            Ninja newKage = ninjaService.getNinjaEntityById(request.kageId());
            validateKageIsNotLeadingAnyVillage(newKage, id);
            existingVillage.setKage(newKage);
        }

        Village updatedVillage = villageRepository.save(existingVillage);
        return villageMapper.entityToDto(updatedVillage);
    }

    @Transactional
    public void deleteVillage(Long id){
        Village villageToDelete = getVillageEntityById(id);

        List<Ninja> ninjasInVillage = ninjaService.getNinjasByVillageId(id);

        ninjasInVillage.forEach(ninja -> ninja.setVillage(null));
        ninjaService.saveAllNinjas(ninjasInVillage);

        villageRepository.delete(villageToDelete);
    }

    private void validateVillageNameNotTaken(String villageName) {
        if (villageRepository.existsByNameIgnoreCase(villageName)) {
            throw new ResourceConflictException("Village with this name already exists: " + villageName);
        }
    }

    private void validateKageAssignment(Long kageId) {
        if (villageRepository.existsByKageId(kageId)) {
            throw new ResourceConflictException("Ninja with ID " + kageId + " is already the Kage of another village.");
        }
    }

    private void validateKageIsNotLeadingAnyVillage(Ninja kage, Long currentVillageId) {
        if (villageRepository.existsByKageAndIdNot(kage, currentVillageId)) {
            throw new ResourceConflictException("Ninja with ID " + kage.getId() + " is already the Kage of another village.");
        }
    }
}
