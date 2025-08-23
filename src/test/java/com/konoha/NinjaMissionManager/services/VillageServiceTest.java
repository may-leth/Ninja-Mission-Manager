package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.village.VillageMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
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

    @InjectMocks
    private VillageService villageService;

    private Village village;
    private VillageResponse villageResponse;
    private Ninja kage;
    private VillageRequest villageRequest;

    @BeforeEach
    void setUp(){
        village = new Village(1L, "Konoha", null);
        villageResponse = new VillageResponse(1L, "Konoha", null);
        kage = new Ninja(1L, "Naruto", "naruto@gmail.com", "Naruto12345.", Rank.KAGE, null, 0, false, null, null);
        villageRequest = new VillageRequest("Konoha", 1L);
    }

    @Nested
    @DisplayName("getAllVillages")
    class GetAll {
        @Test
        @DisplayName("should return all villages when no filter is provided")
        void shouldReturnAllVillagesWithoutFilters() {
            List<Village> villageList = List.of(village);
            when(villageRepository.findAll(any(Specification.class))).thenReturn(villageList);
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(villageResponse);

            List<VillageResponse> result = villageService.getAllVillages(Optional.empty());

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(villageResponse.name(), result.getFirst().name());
        }

        @Test
        @DisplayName("should return filtered villages when kage name is provided")
        void shouldReturnFilteredByKageName() {
            List<Village> villageList = List.of(village);
            when(villageRepository.findAll(any(Specification.class))).thenReturn(villageList);
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(villageResponse);

            List<VillageResponse> result = villageService.getAllVillages(Optional.of("Naruto"));

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(villageResponse.name(), result.getFirst().name());
        }

        @Test
        @DisplayName("should return empty list when no villages are found with the filter")
        void shouldReturnEmptyListWhenNoVillagesFound() {
            when(villageRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            List<VillageResponse> result = villageService.getAllVillages(Optional.of("NonExistentKage"));

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getVillageResponseById")
    class GetVillageByIdTest {
        @Test
        @DisplayName("should find a village by its ID when it exists")
        void shouldReturnVillageWhenVillageExists() {
            when(villageRepository.findById(anyLong())).thenReturn(Optional.of(village));
            when(villageMapper.entityToDto(any(Village.class))).thenReturn(villageResponse);

            VillageResponse result = villageService.getVillageResponseById(1L);

            assertNotNull(result);
            assertEquals(villageResponse.id(), result.id());
            assertEquals(villageResponse.name(), result.name());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when village does not exist")
        void shouldThrowExceptionWhenVillageDoesNotExist() {
            when(villageRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> villageService.getVillageResponseById(1L));
        }
    }

    @Nested
    @DisplayName("createVillage")
    class CreateVillageTest {
        @Test
        @DisplayName("Should create a new village successfully")
        void shouldCreateVillageSuccessfully(){
            when(villageRepository.existsByNameIgnoreCase(villageRequest.name())).thenReturn(false);
            when(villageRepository.existsByKageId(kage.getId())).thenReturn(false);
            when(villageMapper.dtoToEntity(villageRequest, kage)).thenReturn(village);
            when(villageRepository.save(village)).thenReturn(village);
            when(villageMapper.entityToDto(village)).thenReturn(villageResponse);

            VillageResponse result = villageService.createVillage(villageRequest, kage);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(villageRequest.name());

            verify(villageRepository).existsByNameIgnoreCase(villageRequest.name());
            verify(villageRepository).existsByKageId(kage.getId());
            verify(villageRepository).save(village);
            verify(villageMapper).dtoToEntity(villageRequest, kage);
            verify(villageMapper).entityToDto(village);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when village name already exists")
        void shouldThrowExceptionWhenVillageNameExists() {
            when(villageRepository.existsByNameIgnoreCase(villageRequest.name())).thenReturn(true);

            assertThatThrownBy(() -> villageService.createVillage(villageRequest, kage))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Village with this name already exists");

            verify(villageRepository).existsByNameIgnoreCase(villageRequest.name());
            verify(villageRepository, never()).existsByKageId(anyLong());
            verify(villageRepository, never()).save(any(Village.class));
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when Kage is already leading a village")
        void shouldThrowExceptionWhenKageAlreadyHasAVillage() {
            when(villageRepository.existsByNameIgnoreCase(villageRequest.name())).thenReturn(false);
            when(villageRepository.existsByKageId(kage.getId())).thenReturn(true);

            assertThatThrownBy(() -> villageService.createVillage(villageRequest, kage))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("is already the Kage of another village.");

            verify(villageRepository).existsByNameIgnoreCase(villageRequest.name());
            verify(villageRepository).existsByKageId(kage.getId());
            verify(villageRepository, never()).save(any(Village.class));
        }
    }
}
