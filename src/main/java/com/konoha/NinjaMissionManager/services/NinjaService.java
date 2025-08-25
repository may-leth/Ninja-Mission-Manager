package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.*;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import com.konoha.NinjaMissionManager.security.NinjaUserDetail;
import com.konoha.NinjaMissionManager.specifications.NinjaSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NinjaService implements UserDetailsService {
    private final NinjaRepository ninjaRepository;
    private final NinjaMapper ninjaMapper;
    private final MissionMapper missionMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VillageService villageService;

    public List<NinjaResponse> getAllNinjas(Optional<Rank> rank, Optional<Long> villageId, Optional<Boolean> isAnbu, Principal principal){
        Ninja authenticatedNinja = getAuthenticatedNinja(principal);
        validateKageAccess(authenticatedNinja);

        Specification<Ninja> specification = NinjaSpecificationBuilder.builder()
                .rank(rank)
                .villageId(villageId)
                .isAnbu(isAnbu)
                .build();

        return ninjaRepository.findAll(specification)
                .stream()
                .map(ninja -> ninjaMapper.entityToDto(ninja, missionMapper))
                .toList();
    }

    public NinjaResponse getNinjaById(Long requestedId, Principal principal) {
        Ninja authenticatedNinja = getAuthenticatedNinja(principal);
        validateOwnerOrKageAccess(requestedId, authenticatedNinja);

        Ninja ninja = findNinjaById(requestedId);
        return ninjaMapper.entityToDto(ninja, missionMapper);
    }

    public Ninja getNinjaEntityById(Long id) {
        return ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with id " + id));
    }

    public List<Ninja> getNinjasByVillageId(Long villageId) {
        return ninjaRepository.findByVillageId(villageId);
    }

    @Transactional
    public NinjaResponse registerNewNinja(NinjaRegisterRequest request){
        validateEmailNotTaken(request.email());

        Ninja ninjaToSave = Ninja.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rank(Rank.GENIN)
                .village(villageService.getVillageEntityById(request.villageId()))
                .isAnbu(false)
                .roles(Set.of(Role.ROLE_NINJA_USER))
                .missionsCompletedCount(0)
                .build();

        return persistAndMapNinja(ninjaToSave);
    }

    @Transactional
    public NinjaResponse createNinja(KageCreateNinjaRequest request){
        validateEmailNotTaken(request.email());

        Ninja ninjaToSave = Ninja.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .rank(request.rank())
                .village(villageService.getVillageEntityById(request.villageId()))
                .isAnbu(request.isAnbu())
                .roles(request.roles())
                .missionsCompletedCount(0)
                .build();

        return persistAndMapNinja(ninjaToSave);
    }

    @Transactional
    public NinjaResponse updateNinja(Long requestedId, NinjaSelfUpdateRequest request, Principal principal) {
        Ninja authenticatedNinja = getAuthenticatedNinja(principal);
        validateOwnerAccess(requestedId, authenticatedNinja);

        Ninja ninjaToUpdate = findNinjaById(requestedId);
        validateEmailChange(request.email(), ninjaToUpdate.getEmail());

        ninjaToUpdate.setName(request.name());
        ninjaToUpdate.setEmail(request.email());
        ninjaToUpdate.setPassword(passwordEncoder.encode(request.password()));

        return persistAndMapNinja(ninjaToUpdate);
    }

    @Transactional
    public NinjaResponse updateAsKage(Long requestedId, NinjaKageUpdateRequest request, Principal principal) {
        Ninja authenticatedNinja = getAuthenticatedNinja(principal);
        validateKageAccess(authenticatedNinja);

        Ninja ninjaToUpdate = findNinjaById(requestedId);
        validateEmailChange(request.email(), ninjaToUpdate.getEmail());

        ninjaToUpdate.setName(request.name());
        ninjaToUpdate.setEmail(request.email());
        ninjaToUpdate.setRank(request.rank());
        ninjaToUpdate.setAnbu(request.isAnbu());
        if (request.villageId() != null) {
            ninjaToUpdate.setVillage(villageService.getVillageEntityById(request.villageId()));
        }
        ninjaToUpdate.setRoles(request.roles());

        return persistAndMapNinja(ninjaToUpdate);
    }

    @Transactional
    public void deleteNinja(Long requestedId, Principal principal){
        Ninja authenticatedNinja = getAuthenticatedNinja(principal);
        validateOwnerOrKageAccess(requestedId, authenticatedNinja);

        Ninja ninjaToDelete = findNinjaById(requestedId);

        for (Mission mission : ninjaToDelete.getAssignedMissions()) {
            mission.getAssignedNinjas().remove(ninjaToDelete);
        }
        ninjaToDelete.getAssignedMissions().clear();

        ninjaRepository.delete(ninjaToDelete);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return ninjaRepository.findByEmail(email)
                .map(ninja -> new NinjaUserDetail(ninja))
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with the email: " + email));
    }

    public void saveAllNinjas(List<Ninja> ninjas){
        ninjaRepository.saveAll(ninjas);
    }

    public Ninja getAuthenticatedNinja(Principal principal) {
        String authenticatedEmail = principal.getName();
        return ninjaRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with the email: " + authenticatedEmail));
    }

    private Ninja findNinjaById(Long id) {
        return ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with ID: " + id));
    }

    private void validateKageAccess(Ninja ninja) {
        boolean isKage = ninja.getRoles().stream().anyMatch(role -> role.equals(Role.ROLE_KAGE));
        if (!isKage) {
            throw new AccessDeniedException("You are not authorized to perform this operation.");
        }
    }

    private void validateOwnerAccess(Long requestedId, Ninja authenticatedNinja) {
        if (!requestedId.equals(authenticatedNinja.getId())) {
            throw new AccessDeniedException("You are not authorized to update this ninja's data.");
        }
    }

    private void validateOwnerOrKageAccess(Long requestedId, Ninja authenticatedNinja) {
        boolean isKage = authenticatedNinja.getRoles().stream().anyMatch(role -> role.equals(Role.ROLE_KAGE));
        boolean isOwner = requestedId.equals(authenticatedNinja.getId());
        if (!isOwner && !isKage) {
            throw new AccessDeniedException("You are not authorized to view this ninja's data.");
        }
    }

    private void validateEmailNotTaken(String email) {
        if (ninjaRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already registered: " + email);
        }
    }

    private void validateEmailChange(String newEmail, String currentEmail) {
        if (!newEmail.equals(currentEmail) && ninjaRepository.existsByEmail(newEmail)) {
            throw new ResourceConflictException("Email is already taken");
        }
    }

    private NinjaResponse persistAndMapNinja(Ninja ninja) {
        Ninja savedNinja = ninjaRepository.save(ninja);
        return ninjaMapper.entityToDto(savedNinja, missionMapper);
    }
}