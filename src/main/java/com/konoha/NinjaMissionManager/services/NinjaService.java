package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.KageCreateNinjaRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaRegisterRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import com.konoha.NinjaMissionManager.security.NinjaUserDetail;
import com.konoha.NinjaMissionManager.specifications.NinjaSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NinjaService implements UserDetailsService {
    private final NinjaRepository ninjaRepository;
    private final NinjaMapper ninjaMapper;
    private final MissionMapper missionMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VillageService villageService;

    public List<NinjaResponse> getAllNinjas(Optional<Rank> rank, Optional<Long> villageId, Optional<Boolean> isAnbu){
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

    public NinjaResponse getNinjaById(Long id) {
        Ninja ninja = ninjaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with ID: " + id));
        return ninjaMapper.entityToDto(ninja, missionMapper);
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

        return saveAndMapNinja(ninjaToSave);
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

        return saveAndMapNinja(ninjaToSave);
    }

    private void validateEmailNotTaken(String email) {
        if (ninjaRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already registered: " + email);
        }
    }

    private NinjaResponse saveAndMapNinja(Ninja ninja) {
        Ninja savedNinja = ninjaRepository.save(ninja);
        return ninjaMapper.entityToDto(savedNinja, missionMapper);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return ninjaRepository.findByEmail(email)
                .map(ninja -> new NinjaUserDetail(ninja))
                .orElseThrow(() -> new ResourceNotFoundException("Ninja not found with the email: " + email));
    }
}
