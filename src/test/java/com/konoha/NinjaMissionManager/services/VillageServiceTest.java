package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
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

import java.util.*;

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
    @DisplayName("createVillageInternal")
    class CreateVillageInternalTest {
        @Test
        @DisplayName("Should create and save a new village successfully")
        void shouldCreateAndSaveVillageSuccessfully() {
            when(villageMapper.dtoToEntity(konohaRequest, naruto)).thenReturn(konoha);
            when(villageRepository.save(konoha)).thenReturn(konoha);
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            VillageResponse result = villageService.createVillageInternal(konohaRequest, naruto);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Konoha");

            verify(villageMapper).dtoToEntity(konohaRequest, naruto);
            verify(villageRepository).save(konoha);
            verify(villageMapper).entityToDto(konoha);
            verifyNoMoreInteractions(villageMapper, villageRepository);
        }
    }

    @Nested
    @DisplayName("UpdateVillageIternalTest")
    class UpdateVillageInternalTest{
        @Test
        @DisplayName("Should update and save a village successfully")
        void shouldUpdateAndSaveVillageSuccessfully(){
            when(villageRepository.save(konoha)).thenReturn(konoha);
            when(villageMapper.entityToDto(konoha)).thenReturn(konohaResponse);

            VillageResponse result = villageService.updateVillageInternal(konoha);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);

            verify(villageRepository).save(konoha);
            verify(villageMapper).entityToDto(konoha);
            verifyNoMoreInteractions(villageRepository, villageMapper);
        }
    }

    @Nested
    @DisplayName("DeleteVillageInternal")
    class DeleteVillageInternalTest {
        @Test
        @DisplayName("Should delete a village successfully")
        void  shouldDeleteVillageSuccessfully(){
            doNothing().when(villageRepository).delete(konoha);

            villageService.deleteVillageInternal(konoha);

            verify(villageRepository, times(1)).delete(konoha);
            verifyNoMoreInteractions(villageRepository);
        }
    }
}
