package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.ninja.KageCreateNinjaRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaKageUpdateRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaRegisterRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NinjaVillageCoordinatorService {
    private final NinjaService ninjaService;
    private final VillageService villageService;
    private final EmailService emailService;

    @Transactional
    public VillageResponse createVillage(VillageRequest request){
        Ninja kage = ninjaService.getNinjaEntityById(request.kageId());

        villageService.validateKageAssignment(kage.getId());
        villageService.validateVillageNameNotTaken(request.name());

        return villageService.createVillageInternal(request, kage);
    }

    @Transactional
    public VillageResponse updateVillage(Long id, VillageUpdateRequest request){
        Village existingVillage = villageService.getVillageEntityById(id);

        if (request.name() != null && !request.name().equalsIgnoreCase(existingVillage.getName())){
            villageService.validateVillageNameNotTaken(request.name());
            existingVillage.setName(request.name());
        }
        if (request.kageId() != null){
            Ninja newKage = ninjaService.getNinjaEntityById(request.kageId());
            villageService.validateKageIsNotLeadingAnyVillage(newKage, id);
            existingVillage.setKage(newKage);
        }

        return villageService.updateVillageInternal(existingVillage);
    }

    @Transactional
    public void deleteVillage(Long id) {
        Village villageToDelete = villageService.getVillageEntityById(id);

        List<Ninja> ninjasInVillage = ninjaService.getNinjasByVillageId(id);

        ninjasInVillage.forEach(ninja -> ninja.setVillage(null));

        ninjaService.saveAllNinjas(ninjasInVillage);

        villageService.deleteVillageInternal(villageToDelete);
    }

    @Transactional
    public NinjaResponse registerNewNinja(NinjaRegisterRequest request){
        Village village = villageService.getVillageEntityById(request.villageId());

        NinjaResponse newNinja = ninjaService.registerNewNinjaInternal(request, village);

        emailService.sendNinjaWelcomeEmail(
                newNinja.email(),
                newNinja.name(),
                village.getName()
        );

        return newNinja;
    }

    @Transactional
    public NinjaResponse createNinja(KageCreateNinjaRequest request){
        Village village = villageService.getVillageEntityById(request.villageId());
        return ninjaService.createNinjaInternal(request, village);
    }

    @Transactional
    public NinjaResponse updateAsKage(Long requestedId, NinjaKageUpdateRequest request, Principal principal){
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        ninjaService.validateKageAccess(authenticatedNinja);

        Ninja ninjaToUpdate = ninjaService.getNinjaEntityById(requestedId);

        ninjaService.validateEmailChange(request.email(), ninjaToUpdate.getEmail());

        Village newVillage = null;
        if (request.villageId() != null){
            newVillage = villageService.getVillageEntityById(request.villageId());
        }

        return ninjaService.updateAsKageInternal(ninjaToUpdate.getId(), request, newVillage);
    }
}
