package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Village;
import com.konoha.NinjaMissionManager.repositories.VillageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for VillageServices")
public class VillageServiceTest {
    @Mock
    private VillageRepository villageRepository;

    @Mock
    private VillageMapper villageMapper;

    @Mock
    private NinjaService ninjaService;

    @InjectMocks
    private VillageService villageService;

    private Village konoha;
    private Village kumo;
    private Ninja naruto;
    private Ninja raikage;
    private VillageRequest konohaRequest;
    private VillageResponse konohaResponse;

    @BeforeEach
    void setUp(){
        naruto = new Ninja();
        naruto.setId(1L);
        raikage = new Ninja();
        raikage.setId(2L);

        konoha = new Village(1L, "Konoha", naruto);
        kumo = new Village(2L, "Kumo", raikage);

        konohaRequest = new VillageRequest("Konoha", 1L);
        konohaResponse = new VillageResponse(1L, "Konoha", "Naruto Uzumaki");
    }

    @Nested
    @DisplayName("getAllVillages")
    class GetAll {
        @Test
        @DisplayName("should return all villages when no filter is provided")
        void shouldReturnAllVillagesWithoutFilters() {
            when(villageRepository.findAll(any(Specification.class))).thenReturn(List.of(konoha, kumo));
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);
            when(villageMapper.entityToDto(kumo)).thenReturn(new VillageResponse(2L, "Kumo", "Raikage"));

            List<VillageResponse> result = villageService.getAllVillages(Optional.empty());

            assertThat(result).hasSize(2);
            verify(villageRepository).findAll(any(Specification.class));
            verify(villageMapper, times(2)).entityToDto(any(Village.class));
        }

        @Test
        @DisplayName("should return filtered villages when kage name is provided")
        void shouldReturnFilteredByKageName() {
            when(villageRepository.findAll(any(Specification.class))).thenReturn(List.of(konoha));
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            List<VillageResponse> result = villageService.getAllVillages(Optional.of("Naruto Uzumaki"));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).kage()).isEqualTo("Naruto Uzumaki");
            verify(villageRepository).findAll(any(Specification.class));
            verify(villageMapper).entityToDto(konoha);
        }

        @Test
        @DisplayName("should return empty list when no villages are found with the filter")
        void shouldReturnEmptyListWhenNoVillagesFound() {
            when(villageRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            List<VillageResponse> result = villageService.getAllVillages(Optional.empty());

            assertThat(result).isEmpty();
            verify(villageRepository).findAll(any(Specification.class));
            verifyNoInteractions(villageMapper);
        }
    }

    @Nested
    @DisplayName("getVillageResponseById")
    class GetVillageByIdTest {
        @Test
        @DisplayName("should find a village by its ID when it exists")
        void shouldReturnVillageWhenVillageExists() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            VillageResponse result = villageService.getVillageResponseById(1L);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Konoha");
            verify(villageRepository).findById(1L);
            verify(villageMapper).entityToDto(konoha);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when village does not exist")
        void shouldThrowExceptionWhenVillageDoesNotExist() {
            when(villageRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> villageService.getVillageResponseById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageRepository).findById(99L);
            verifyNoInteractions(villageMapper);
        }
    }

    @Nested
    @DisplayName("createVillage")
    class CreateVillageTest {
        @Test
        @DisplayName("Should create a new village successfully")
        void shouldCreateVillageSuccessfully(){
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenReturn(naruto);
            when(villageRepository.existsByNameIgnoreCase(konohaRequest.name())).thenReturn(false);
            when(villageRepository.existsByKageId(naruto.getId())).thenReturn(false);
            when(villageMapper.dtoToEntity(konohaRequest, naruto)).thenReturn(konoha);
            when(villageRepository.save(konoha)).thenReturn(konoha);
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            VillageResponse result = villageService.createVillage(konohaRequest);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(konohaRequest.name());

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageRepository).existsByNameIgnoreCase(konohaRequest.name());
            verify(villageRepository).existsByKageId(naruto.getId());
            verify(villageRepository).save(konoha);
            verify(villageMapper).dtoToEntity(konohaRequest, naruto);
            verify(villageMapper).entityToDto(konoha);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when village name already exists")
        void shouldThrowExceptionWhenVillageNameExists() {
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenThrow(new ResourceNotFoundException("Ninja not found"));

            assertThatThrownBy(() -> villageService.createVillage(konohaRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found");

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageRepository, never()).existsByNameIgnoreCase(any());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when Kage is already leading a village")
        void shouldThrowExceptionWhenKageAlreadyHasAVillage() {
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenReturn(naruto);
            when(villageRepository.existsByNameIgnoreCase(konohaRequest.name())).thenReturn(false);
            when(villageRepository.existsByKageId(naruto.getId())).thenReturn(true);

            assertThatThrownBy(() -> villageService.createVillage(konohaRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("is already the Kage of another village.");

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageRepository).existsByNameIgnoreCase(konohaRequest.name());
            verify(villageRepository).existsByKageId(naruto.getId());
            verify(villageRepository, never()).save(any(Village.class));
        }
    }

    @Nested
    @DisplayName("updateVillage")
    class UpdateVillageTest {
        private VillageUpdateRequest updateNameRequest;
        private VillageUpdateRequest updateKageRequest;
        private VillageUpdateRequest updateBothRequest;

        @BeforeEach
        void setup() {
            updateNameRequest = new VillageUpdateRequest("Shinobi", null);
            updateKageRequest = new VillageUpdateRequest(null, 2L);
            updateBothRequest = new VillageUpdateRequest("Shinobi", 2L);
        }

        @Test
        @DisplayName("Should update village successfully when name and kage are updated")
        void shouldUpdateVillageWhenNameAndKageAreUpdated() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(villageRepository.existsByNameIgnoreCase(updateBothRequest.name())).thenReturn(false);
            when(ninjaService.getNinjaEntityById(updateBothRequest.kageId())).thenReturn(raikage);
            when(villageRepository.existsByKageAndIdNot(raikage, 1L)).thenReturn(false);
            when(villageRepository.save(any(Village.class))).thenReturn(konoha);
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(konohaResponse);

            VillageResponse result = villageService.updateVillage(1L, updateBothRequest);

            assertThat(result).isNotNull();
            verify(villageRepository).findById(1L);
            verify(villageRepository).existsByNameIgnoreCase(updateBothRequest.name());
            verify(ninjaService).getNinjaEntityById(updateBothRequest.kageId());
            verify(villageRepository).existsByKageAndIdNot(raikage, 1L);
            verify(villageRepository).save(any(Village.class));
            verify(villageMapper).entityToDto(any(Village.class));
        }

        @Test
        @DisplayName("Should update village successfully when only name is updated")
        void shouldUpdateVillageWhenOnlyNameIsUpdated() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(villageRepository.existsByNameIgnoreCase(updateNameRequest.name())).thenReturn(false);
            when(villageRepository.save(any(Village.class))).thenReturn(konoha);
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(konohaResponse);

            VillageResponse result = villageService.updateVillage(1L, updateNameRequest);

            assertThat(result).isNotNull();
            verify(villageRepository).findById(1L);
            verify(villageRepository).existsByNameIgnoreCase(updateNameRequest.name());
            verify(villageRepository).save(any(Village.class));
            verifyNoInteractions(ninjaService);
            verify(villageRepository, never()).existsByKageAndIdNot(any(), anyLong());
        }

        @Test
        @DisplayName("Should update village successfully when only kage is updated")
        void shouldUpdateVillageWhenOnlyKageIsUpdated() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId())).thenReturn(raikage);
            when(villageRepository.existsByKageAndIdNot(raikage, 1L)).thenReturn(false);
            when(villageRepository.save(any(Village.class))).thenReturn(konoha);
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(konohaResponse);

            VillageResponse result = villageService.updateVillage(1L, updateKageRequest);

            assertThat(result).isNotNull();
            verify(villageRepository).findById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageRepository).existsByKageAndIdNot(raikage, 1L);
            verify(villageRepository).save(any(Village.class));
            verify(villageMapper).entityToDto(any(Village.class));
            verify(villageRepository, never()).existsByNameIgnoreCase(any());
        }

        @Test
        @DisplayName("Should not update village when no fields are provided")
        void shouldNotUpdateVillageWhenNoFieldsProvided() {
            VillageUpdateRequest emptyRequest = new VillageUpdateRequest(null, null);
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(villageRepository.save(konoha)).thenReturn(konoha);
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            VillageResponse result = villageService.updateVillage(1L, emptyRequest);

            assertThat(result).isNotNull();
            verify(villageRepository).findById(1L);
            verify(villageRepository).save(konoha);
            verify(villageMapper).entityToDto(konoha);
            verifyNoMoreInteractions(villageRepository, ninjaService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when village to update does not exist")
        void shouldThrowNotFoundExceptionWhenVillageDoesNotExist() {
            when(villageRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> villageService.updateVillage(99L, updateBothRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageRepository).findById(99L);
            verify(villageRepository, never()).save(any(Village.class));
            verifyNoInteractions(ninjaService, villageMapper);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when updated name already exists")
        void shouldThrowConflictWhenUpdatedNameExists() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(villageRepository.existsByNameIgnoreCase(updateNameRequest.name())).thenReturn(true);

            assertThatThrownBy(() -> villageService.updateVillage(1L, updateNameRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Village with this name already exists");

            verify(villageRepository).findById(1L);
            verify(villageRepository).existsByNameIgnoreCase(updateNameRequest.name());
            verify(villageRepository, never()).save(any(Village.class));
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when new kage does not exist")
        void shouldThrowNotFoundExceptionWhenNewKageDoesNotExist() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId())).thenThrow(new ResourceNotFoundException("Ninja not found"));

            assertThatThrownBy(() -> villageService.updateVillage(1L, updateKageRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found");

            verify(villageRepository).findById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageRepository, never()).save(any(Village.class));
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when new kage is already leading another village")
        void shouldThrowConflictWhenNewKageAlreadyLeadsAnotherVillage() {
            when(villageRepository.findById(1L)).thenReturn(Optional.of(konoha));
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId())).thenReturn(raikage);
            when(villageRepository.existsByKageAndIdNot(raikage, 1L)).thenReturn(true);

            assertThatThrownBy(() -> villageService.updateVillage(1L, updateKageRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("is already the Kage of another village.");

            verify(villageRepository).findById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageRepository).existsByKageAndIdNot(raikage, 1L);
            verify(villageRepository, never()).save(any(Village.class));
        }
    }
}
