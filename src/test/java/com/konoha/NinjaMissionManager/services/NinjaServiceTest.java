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
import com.konoha.NinjaMissionManager.models.Village;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for NinjaServices")
public class NinjaServiceTest {
    @Mock
    private NinjaRepository ninjaRepository;

    @Mock
    private NinjaMapper ninjaMapper;

    @Mock
    private MissionMapper missionMapper;

    @Mock
    private VillageService villageService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private Principal principal;

    @InjectMocks
    private NinjaService ninjaService;

    private Ninja naruto;
    private Ninja sasuke;
    private Ninja kage;
    private NinjaResponse narutoResponse;
    private NinjaResponse sasukeResponse;
    private NinjaResponse kageResponse;

    @BeforeEach
    void setUp() {
        naruto = new Ninja(1L, "Naruto Uzumaki", "naruto@konoha.com", "pass", Rank.GENIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        sasuke = new Ninja(2L, "Sasuke Uchiha", "sasuke@konoha.com", "pass", Rank.GENIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        kage = new Ninja(3L, "Tsunade Senju", "tsunade@konoha.com", "pass", Rank.KAGE, new Village(), 0, false, Set.of(Role.ROLE_KAGE), Collections.emptySet());

        narutoResponse = new NinjaResponse(1L, "Naruto Uzumaki", "naruto@konoha.com", "GENIN", "Konoha", 0, false, Collections.emptySet());
        sasukeResponse = new NinjaResponse(2L, "Sasuke Uchiha", "sasuke@konoha.com", "GENIN", "Konoha", 0, false, Collections.emptySet());
        kageResponse = new NinjaResponse(3L, "Tsunade Senju", "tsunade@konoha.com", "KAGE", "Konoha", 0, false, Collections.emptySet());
    }

    @Nested
    @DisplayName("getAllNinjas")
    class GetAllNinjasTests {

        @Nested
        @DisplayName("Security Logic")
        class SecurityLogic {
            @Test
            @DisplayName("Should throw AccessDeniedException when user is not a Kage")
            void shouldThrowAccessDeniedWhenNotKage() {
                when(principal.getName()).thenReturn(naruto.getEmail());
                when(ninjaRepository.findByEmail(naruto.getEmail())).thenReturn(Optional.of(naruto));

                assertThatThrownBy(() -> ninjaService.getAllNinjas(Optional.empty(), Optional.empty(), Optional.empty(), principal))
                        .isInstanceOf(AccessDeniedException.class)
                        .hasMessageContaining("You are not authorized to view the list of ninjas.");

                verify(ninjaRepository).findByEmail(naruto.getEmail());
                verify(ninjaRepository, never()).findAll(any(Specification.class));
                verifyNoInteractions(ninjaMapper);
            }
        }

        @Nested
        @DisplayName("Filtering Logic")
        class FilteringLogic {
            @BeforeEach
            void mockKagePrincipal() {
                when(principal.getName()).thenReturn(kage.getEmail());
                when(ninjaRepository.findByEmail(kage.getEmail())).thenReturn(Optional.of(kage));
            }

            @Test
            @DisplayName("Should return all ninjas without filters when user is a Kage")
            void shouldReturnAllWithoutFilters() {
                when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(naruto, sasuke));
                when(ninjaMapper.entityToDto(eq(naruto), any())).thenReturn(narutoResponse);
                when(ninjaMapper.entityToDto(eq(sasuke), any())).thenReturn(sasukeResponse);

                List<NinjaResponse> result = ninjaService.getAllNinjas(Optional.empty(), Optional.empty(), Optional.empty(), principal);

                assertThat(result).hasSize(2).containsExactly(narutoResponse, sasukeResponse);
                verify(ninjaRepository).findByEmail(kage.getEmail());
                verify(ninjaRepository).findAll(any(Specification.class));
                verify(ninjaMapper, times(2)).entityToDto(any(Ninja.class), any());
            }

            @Test
            @DisplayName("Should return ninjas filtered by rank")
            void shouldReturnFilteredByRank() {
                when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(naruto));
                when(ninjaMapper.entityToDto(naruto, missionMapper)).thenReturn(narutoResponse);

                List<NinjaResponse> result = ninjaService.getAllNinjas(
                        Optional.of(Rank.GENIN),
                        Optional.empty(),
                        Optional.empty(),
                        principal);

                assertThat(result).hasSize(1).containsExactly(narutoResponse);
                verify(ninjaRepository).findByEmail(kage.getEmail());
                verify(ninjaRepository).findAll(any(Specification.class));
            }

            @Test
            @DisplayName("Should return ninjas filtered by village")
            void shouldReturnFilteredByVillage() {
                when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(naruto));
                when(ninjaMapper.entityToDto(naruto, missionMapper)).thenReturn(narutoResponse);

                List<NinjaResponse> result = ninjaService.getAllNinjas(
                        Optional.empty(),
                        Optional.of(1L),
                        Optional.empty(),
                        principal);

                assertThat(result).hasSize(1).containsExactly(narutoResponse);
                verify(ninjaRepository).findByEmail(kage.getEmail());
                verify(ninjaRepository).findAll(any(Specification.class));
            }

            @Test
            @DisplayName("Should return ninjas filtered by if is Anbu")
            void shouldReturnFilteredByIsAnbu() {
                when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(naruto));
                when(ninjaMapper.entityToDto(naruto, missionMapper)).thenReturn(narutoResponse);

                List<NinjaResponse> result = ninjaService.getAllNinjas(
                        Optional.empty(),
                        Optional.empty(),
                        Optional.of(true),
                        principal);

                assertThat(result).hasSize(1).containsExactly(narutoResponse);
                verify(ninjaRepository).findByEmail(kage.getEmail());
                verify(ninjaRepository).findAll(any(Specification.class));
            }

            @Test
            @DisplayName("Should return empty list when no results match filters")
            void shouldReturnEmptyWhenNoMatch() {
                when(ninjaRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

                List<NinjaResponse> result = ninjaService.getAllNinjas(
                        Optional.of(Rank.GENIN),
                        Optional.of(99L),
                        Optional.of(false),
                        principal);

                assertThat(result).isEmpty();
                verify(ninjaRepository).findByEmail(kage.getEmail());
                verify(ninjaRepository).findAll(any(Specification.class));
                verifyNoInteractions(ninjaMapper);
            }
        }
    }


    @Nested
    @DisplayName("getNinjaById")
    class GetNinjaByIdTests {
        @Test
        @DisplayName("Should return ninja when authenticated user is the owner")
        void shouldReturnNinjaForOwner() {
            when(principal.getName()).thenReturn(naruto.getEmail());
            when(ninjaRepository.findByEmail(naruto.getEmail())).thenReturn(Optional.of(naruto));
            when(ninjaRepository.findById(naruto.getId())).thenReturn(Optional.of(naruto));
            when(ninjaMapper.entityToDto(eq(naruto), any())).thenReturn(narutoResponse);

            NinjaResponse result = ninjaService.getNinjaById(naruto.getId(), principal);

            assertThat(result).isEqualTo(narutoResponse);
            verify(ninjaRepository).findByEmail(naruto.getEmail());
            verify(ninjaRepository).findById(naruto.getId());
            verify(ninjaMapper).entityToDto(eq(naruto), any());
        }

        @Test
        @DisplayName("Should return ninja when authenticated user is Kage")
        void shouldReturnNinjaForKage() {
            when(principal.getName()).thenReturn(kage.getEmail());
            when(ninjaRepository.findByEmail(kage.getEmail())).thenReturn(Optional.of(kage));
            when(ninjaRepository.findById(naruto.getId())).thenReturn(Optional.of(naruto));
            when(ninjaMapper.entityToDto(eq(naruto), any())).thenReturn(narutoResponse);

            NinjaResponse result = ninjaService.getNinjaById(naruto.getId(), principal);

            assertThat(result).isEqualTo(narutoResponse);
            verify(ninjaRepository).findByEmail(kage.getEmail());
            verify(ninjaRepository).findById(naruto.getId());
            verify(ninjaMapper).entityToDto(eq(naruto), any());
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when ninja tries to view another ninja's profile")
        void shouldThrowAccessDeniedForOtherNinja() {
            when(principal.getName()).thenReturn(sasuke.getEmail());
            when(ninjaRepository.findByEmail(sasuke.getEmail())).thenReturn(Optional.of(sasuke));

            assertThatThrownBy(() -> ninjaService.getNinjaById(naruto.getId(), principal))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("You are not authorized to view this ninja's data.");

            verify(ninjaRepository).findByEmail(sasuke.getEmail());
            verify(ninjaRepository, never()).findById(anyLong());
            verifyNoInteractions(ninjaMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when id does not exist")
        void shouldThrowExceptionWhenIdNotFound() {
            when(principal.getName()).thenReturn(kage.getEmail());
            when(ninjaRepository.findByEmail(kage.getEmail())).thenReturn(Optional.of(kage));
            when(ninjaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ninjaService.getNinjaById(99L, principal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found with ID: 99");

            verify(ninjaRepository).findByEmail(kage.getEmail());
            verify(ninjaRepository).findById(99L);
            verifyNoInteractions(ninjaMapper);
        }
    }

    @Nested
    @DisplayName("RegisterNewNinja")
    class RegisterNewNinjaTests {
        @Test
        @DisplayName("Should register a new ninja successfully")
        void shouldRegisterNewNinja(){
            NinjaRegisterRequest request = new NinjaRegisterRequest(
                    "Naruto Uzumaki",
                    "naruto@gmail.com",
                    "Naruto12345.",
                    1L
            );

            Village mockVillage = new Village();
            mockVillage.setId(1L);
            mockVillage.setName("Konoha");

            when(ninjaRepository.existsByEmail("naruto@gmail.com")).thenReturn(false);
            when(passwordEncoder.encode("Naruto12345.")).thenReturn("encodedPassword");
            when(villageService.getVillageEntityById(1L)).thenReturn(mockVillage);

            Ninja savedNinja = Ninja.builder()
                    .id(1L)
                    .name("Naruto Uzumaki")
                    .email("naruto@gmail.com")
                    .password("encodedPassword")
                    .rank(Rank.GENIN)
                    .village(mockVillage)
                    .isAnbu(false)
                    .roles(Set.of(Role.ROLE_NINJA_USER))
                    .missionsCompletedCount(0)
                    .build();

            when(ninjaRepository.save(any(Ninja.class))).thenReturn(savedNinja);
            when(ninjaMapper.entityToDto(savedNinja, missionMapper)).thenReturn(narutoResponse);

            NinjaResponse result = ninjaService.registerNewNinja(request);

            assertThat(result).isEqualTo(narutoResponse);

            verify(ninjaRepository).existsByEmail("naruto@gmail.com");
            verify(passwordEncoder).encode("Naruto12345.");
            verify(villageService).getVillageEntityById(1L);
            verify(ninjaRepository).save(any(Ninja.class));
            verify(ninjaMapper).entityToDto(savedNinja, missionMapper);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when email already exists")
        void shouldThrowWhenEmailExistsRegister() {
            NinjaRegisterRequest request = new NinjaRegisterRequest(
                    "Naruto Uzumaki",
                    "naruto@gmail.com",
                    "Naruto12345.",
                    1L
            );

            when(ninjaRepository.existsByEmail("naruto@gmail.com")).thenReturn(true);

            assertThatThrownBy(() -> ninjaService.registerNewNinja(request))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Email is already registered");

            verify(ninjaRepository).existsByEmail("naruto@gmail.com");
            verifyNoInteractions(passwordEncoder, villageService, ninjaMapper);
            verify(ninjaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("createNinja")
    class CreateNinjaTests{
        @Test
        @DisplayName("Should create a ninja with given attributes succesfully")
        void shouldCreateNinja(){
            KageCreateNinjaRequest request = new KageCreateNinjaRequest(
                    "Itachi Uchiha",
                    "itachi@gmail.com",
                    "Itachi12345.",
                    Rank.JONIN,
                    1L,
                    true,
                    Set.of(Role.ROLE_NINJA_USER, Role.ROLE_KAGE)
            );

            Village mockVillage = new Village();
            mockVillage.setId(1L);
            mockVillage.setName("Konoha");

            when(ninjaRepository.existsByEmail("itachi@gmail.com")).thenReturn(false);
            when(passwordEncoder.encode("Itachi12345.")).thenReturn("encodedPassword");
            when(villageService.getVillageEntityById(1L)).thenReturn(mockVillage);

            Ninja savedNinja = Ninja.builder()
                    .id(2L)
                    .name("Itachi Uchiha")
                    .email("itachi@leaf.com")
                    .password("encodedSharingan")
                    .rank(Rank.JONIN)
                    .village(mockVillage)
                    .isAnbu(true)
                    .roles(Set.of(Role.ROLE_NINJA_USER, Role.ROLE_KAGE))
                    .missionsCompletedCount(0)
                    .build();

            when(ninjaRepository.save(any(Ninja.class))).thenReturn(savedNinja);
            when(ninjaMapper.entityToDto(savedNinja, missionMapper)).thenReturn(sasukeResponse);

            NinjaResponse result = ninjaService.createNinja(request);

            assertThat(result).isEqualTo(sasukeResponse);

            verify(ninjaRepository).existsByEmail("itachi@gmail.com");
            verify(passwordEncoder).encode("Itachi12345.");
            verify(villageService).getVillageEntityById(1L);
            verify(ninjaRepository).save(any(Ninja.class));
            verify(ninjaMapper).entityToDto(savedNinja, missionMapper);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when email already exists")
        void shouldThrowWhenEmailExistsCreate() {
            KageCreateNinjaRequest request = new KageCreateNinjaRequest(
                    "Itachi Uchiha",
                    "itachi@gmail.com",
                    "Itachi12345.",
                    Rank.JONIN,
                    1L,
                    true,
                    Set.of(Role.ROLE_NINJA_USER, Role.ROLE_KAGE)
            );

            when(ninjaRepository.existsByEmail("itachi@gmail.com")).thenReturn(true);

            assertThatThrownBy(() -> ninjaService.createNinja(request))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Email is already registered");

            verify(ninjaRepository).existsByEmail("itachi@gmail.com");
            verifyNoInteractions(passwordEncoder, villageService, ninjaMapper);
            verify(ninjaRepository, never()).save(any());
        }
    }
}
