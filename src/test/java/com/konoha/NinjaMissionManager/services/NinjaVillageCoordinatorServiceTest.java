package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.ninja.KageCreateNinjaRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaKageUpdateRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaRegisterRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
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

import java.security.Principal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NinjaVillageCoordinatorServiceTest {
    @Mock
    private VillageRepository villageRepository;

    @Mock
    private VillageMapper villageMapper;

    @Mock
    private VillageService villageService;

    @Mock
    private NinjaService ninjaService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NinjaVillageCoordinatorService ninjaVillageCoordinatorService;


    private Village konoha;
    private Village kumo;
    private Ninja naruto;
    private Ninja raikage;
    private VillageRequest konohaRequest;
    private VillageResponse konohaResponse;
    private NinjaResponse ninjaResponse;
    private NinjaRegisterRequest ninjaRegisterRequest;
    private KageCreateNinjaRequest kageCreateNinjaRequest;

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
        ninjaResponse = new NinjaResponse(1L,"Naruto","naruto@gmail.com","GENIN", konoha.getName(),1,false,Set.of());
        ninjaRegisterRequest = new NinjaRegisterRequest("Naruto", "naruto@gmail.com", "Naruto12345.", 99L);
        kageCreateNinjaRequest = new KageCreateNinjaRequest("May", "may@gmail.com", "May12345.", Rank.GENIN, 99L, false, Set.of());
    }

    @Nested
    @DisplayName("createVillage")
    class CreateVillageTest {
        @Test
        @DisplayName("Should create a new village successfully")
        void shouldCreateVillageSuccessfully(){
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenReturn(naruto);
            doNothing().when(villageService).validateVillageNameNotTaken(konohaRequest.name());
            doNothing().when(villageService).validateKageAssignment(naruto.getId());
            when(villageService.createVillageInternal(konohaRequest, naruto)).thenReturn(konohaResponse);

            VillageResponse result = ninjaVillageCoordinatorService.createVillage(konohaRequest);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo(konohaRequest.name());

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageService).validateVillageNameNotTaken(konohaRequest.name());
            verify(villageService).validateKageAssignment(naruto.getId());
            verify(villageService).createVillageInternal(konohaRequest, naruto);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when village name already exists")
        void shouldThrowExceptionWhenVillageNameExists() {
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenThrow(new ResourceNotFoundException("Ninja not found"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.createVillage(konohaRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found");

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageRepository, never()).existsByNameIgnoreCase(any());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when Kage is already leading a village")
        void shouldThrowExceptionWhenKageAlreadyHasAVillage() {
            when(ninjaService.getNinjaEntityById(konohaRequest.kageId())).thenReturn(naruto);
            doThrow(new ResourceConflictException("is already the Kage of another village."))
                    .when(villageService).validateKageAssignment(1L);


            assertThatThrownBy(() -> ninjaVillageCoordinatorService.createVillage(konohaRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("is already the Kage of another village.");

            verify(ninjaService).getNinjaEntityById(konohaRequest.kageId());
            verify(villageService).validateKageAssignment(konohaRequest.kageId());
            verify(villageService, never()).validateVillageNameNotTaken(anyString());
            verify(villageService, never()).createVillageInternal(konohaRequest, naruto);
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
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            when(ninjaService.getNinjaEntityById(updateBothRequest.kageId())).thenReturn(raikage);

            doNothing().when(villageService).validateVillageNameNotTaken(updateBothRequest.name());
            doNothing().when(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);

            when(villageService.updateVillageInternal(any(Village.class))).thenReturn(konohaResponse);

            VillageResponse result = ninjaVillageCoordinatorService.updateVillage(1L, updateBothRequest);

            assertThat(result).isNotNull();

            verify(villageService).getVillageEntityById(1L);
            verify(villageService).validateVillageNameNotTaken(updateBothRequest.name());
            verify(ninjaService).getNinjaEntityById(updateBothRequest.kageId());
            verify(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);
            verify(villageService).updateVillageInternal(any(Village.class));
        }

        @Test
        @DisplayName("Should update village successfully when only name is updated")
        void shouldUpdateVillageWhenOnlyNameIsUpdated() {
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            doNothing().when(villageService).validateVillageNameNotTaken(updateNameRequest.name());
            when(villageService.updateVillageInternal(any(Village.class))).thenReturn(konohaResponse);

            VillageResponse result = ninjaVillageCoordinatorService.updateVillage(1L, updateNameRequest);

            assertThat(result).isNotNull();

            verify(villageService).getVillageEntityById(1L);
            verify(villageService).validateVillageNameNotTaken(updateNameRequest.name());
            verify(villageService).updateVillageInternal(any(Village.class));
            verifyNoInteractions(ninjaService);
            verify(villageService, never()).validateKageIsNotLeadingAnyVillage(any(), anyLong());
        }

        @Test
        @DisplayName("Should update village successfully when only kage is updated")
        void shouldUpdateVillageWhenOnlyKageIsUpdated() {
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId())).thenReturn(raikage);
            doNothing().when(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);
            when(villageService.updateVillageInternal(konoha)).thenReturn(konohaResponse);

            VillageResponse result = ninjaVillageCoordinatorService.updateVillage(1L, updateKageRequest);

            assertThat(result).isNotNull();

            verify(villageService).getVillageEntityById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);
            verify(villageService).updateVillageInternal(any(Village.class));
        }

        @Test
        @DisplayName("Should not update village when no fields are provided")
        void shouldNotUpdateVillageWhenNoFieldsProvided() {
            VillageUpdateRequest emptyRequest = new VillageUpdateRequest(null, null);
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            when(villageService.updateVillageInternal(konoha)).thenReturn(konohaResponse);


            VillageResponse result = ninjaVillageCoordinatorService.updateVillage(1L, emptyRequest);

            assertThat(result).isNotNull();

            verify(villageService).getVillageEntityById(1L);
            verify(villageService).updateVillageInternal(konoha);
            verifyNoMoreInteractions(villageService, ninjaService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when village to update does not exist")
        void shouldThrowNotFoundExceptionWhenVillageDoesNotExist() {
            when(villageService.getVillageEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Village not found with ID: 99"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateVillage(99L, updateBothRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageService).getVillageEntityById(99L);
            verify(villageService, never()).updateVillageInternal(any());
            verifyNoInteractions(ninjaService, villageMapper);
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when updated name already exists")
        void shouldThrowConflictWhenUpdatedNameExists() {
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            doThrow(new ResourceConflictException("Village with this name already exists"))
                    .when(villageService).validateVillageNameNotTaken(updateNameRequest.name());

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateVillage(1L, updateNameRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Village with this name already exists");

            verify(villageService).getVillageEntityById(1L);
            verify(villageService).validateVillageNameNotTaken(updateNameRequest.name());
            verify(villageService, never()).updateVillageInternal(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when new kage does not exist")
        void shouldThrowNotFoundExceptionWhenNewKageDoesNotExist() {
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId()))
                    .thenThrow(new ResourceNotFoundException("Ninja not found"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateVillage(1L, updateKageRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found");

            verify(villageService).getVillageEntityById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageService, never()).updateVillageInternal(any());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when new kage is already leading another village")
        void shouldThrowConflictWhenNewKageAlreadyLeadsAnotherVillage() {
            when(villageService.getVillageEntityById(1L)).thenReturn(konoha);
            when(ninjaService.getNinjaEntityById(updateKageRequest.kageId())).thenReturn(raikage);
            doThrow(new ResourceConflictException("is already the Kage of another village."))
                    .when(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateVillage(1L, updateKageRequest))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("is already the Kage of another village.");

            verify(villageService).getVillageEntityById(1L);
            verify(ninjaService).getNinjaEntityById(updateKageRequest.kageId());
            verify(villageService).validateKageIsNotLeadingAnyVillage(raikage, 1L);
            verify(villageService, never()).updateVillageInternal(any());
        }
    }

    @Nested
    @DisplayName("deleteVillage")
    class DeleteVillageTest {
        private Village villageToDelete;
        private Ninja ninja1;
        private Ninja ninja2;

        @BeforeEach
        void setup() {
            ninja1 = new Ninja(2L, "Ninja Test 1", "test1@konoha.com", "password", Rank.GENIN, null, 0, false, new HashSet<>(), new HashSet<>());
            ninja2 = new Ninja(3L, "Ninja Test 2", "test2@konoha.com", "password", Rank.CHUNIN, null, 0, false, new HashSet<>(), new HashSet<>());

            villageToDelete = new Village(1L, "Konoha", ninja1);

            ninja1.setVillage(villageToDelete);
            ninja2.setVillage(villageToDelete);
        }

        @Test
        @DisplayName("Should delete a village successfully when it has ninjas")
        void shouldDeleteVillageWithNinjasSuccessfully() {
            when(villageService.getVillageEntityById(1L)).thenReturn(villageToDelete);
            when(ninjaService.getNinjasByVillageId(1L)).thenReturn(List.of(ninja1, ninja2));
            doNothing().when(ninjaService).saveAllNinjas(anyList());
            doNothing().when(villageService).deleteVillageInternal(villageToDelete);

            ninjaVillageCoordinatorService.deleteVillage(1L);

            verify(villageService).getVillageEntityById(1L);
            verify(ninjaService).getNinjasByVillageId(1L);
            verify(ninjaService).saveAllNinjas(anyList());
            verify(villageService).deleteVillageInternal(villageToDelete);
        }

        @Test
        @DisplayName("should delete a village successfully when it has no ninjas")
        void shouldDeleteVillageWithNoNinjasSuccessfully() {
            when(villageService.getVillageEntityById(1L)).thenReturn(villageToDelete);
            when(ninjaService.getNinjasByVillageId(1L)).thenReturn(Collections.emptyList());
            doNothing().when(villageService).deleteVillageInternal(villageToDelete);

            ninjaVillageCoordinatorService.deleteVillage(1L);

            verify(villageService).getVillageEntityById(1L);
            verify(ninjaService).getNinjasByVillageId(1L);
            verify(ninjaService).saveAllNinjas(anyList());
            verify(villageService).deleteVillageInternal(villageToDelete);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when village to delete does not exist")
        void shouldThrowExceptionWhenVillageToDeleteDoesNotExist() {
            when(villageService.getVillageEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Village not found with ID: 99"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.deleteVillage(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageService).getVillageEntityById(99L);
            verify(villageService, never()).deleteVillageInternal(any(Village.class));
            verify(ninjaService, never()).getNinjasByVillageId(anyLong());
            verify(ninjaService, never()).saveAllNinjas(anyList());
            verify(villageRepository, never()).delete(any(Village.class));
        }
    }

    @Nested
    @DisplayName("registerNewNinja")
    class RegisterNewNinjaTest{
        @Test
        @DisplayName("Should register a new ninja successfully when village exists")
        void shouldRegisterNewNinjaSuccessfully() {
            when(villageService.getVillageEntityById(anyLong())).thenReturn(konoha);
            when(ninjaService.registerNewNinjaInternal(any(), any(Village.class))).thenReturn(ninjaResponse);

            ninjaVillageCoordinatorService.registerNewNinja(ninjaRegisterRequest);

            verify(villageService).getVillageEntityById(99L);
            verify(ninjaService).registerNewNinjaInternal(ninjaRegisterRequest, konoha);
            verify(emailService).sendNinjaWelcomeEmail(eq(ninjaResponse.email()), eq(ninjaResponse.name()), eq(konoha.getName()));
            verifyNoMoreInteractions(villageService, ninjaService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when village does not exist")
        void shouldThrowExceptionWhenVillageDoesNotExists() {
            when(villageService.getVillageEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Village not found with ID: 99"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.registerNewNinja(ninjaRegisterRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageService).getVillageEntityById(ninjaRegisterRequest.villageId());
            verify(ninjaService, never()).registerNewNinjaInternal(any(), any());
            verifyNoMoreInteractions(villageService, ninjaService);
        }
    }

    @Nested
    @DisplayName("createNinja")
    class CreateNinjaTest {
        @Test
        @DisplayName("Should create a new ninja successfully when village exists")
        void shouldCreateNinjaSuccessfully(){
            when(villageService.getVillageEntityById(anyLong())).thenReturn(kumo);
            when(ninjaService.createNinjaInternal(any(), any(Village.class)))
                    .thenReturn(ninjaResponse);

            ninjaVillageCoordinatorService.createNinja(kageCreateNinjaRequest);

            verify(villageService).getVillageEntityById(99L);
            verify(ninjaService).createNinjaInternal(kageCreateNinjaRequest, kumo);
            verifyNoMoreInteractions(villageService, ninjaService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when village does not exist")
        void shouldThrowExceptionWhenVillageDoesNotExist(){
            when(villageService.getVillageEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Village not found with ID: 99"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.createNinja(kageCreateNinjaRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID: 99");

            verify(villageService).getVillageEntityById(99L);
            verify(ninjaService, never()).createNinjaInternal(any(), any());
            verifyNoMoreInteractions(villageService, ninjaService);
        }
    }

    @Nested
    @DisplayName("UpdateAsKage")
    class UpdateAsKageTest {
        private Principal mockPrincipal;
        private Ninja ninjaToUpdate;
        private NinjaKageUpdateRequest updateRequestWithVillage;
        private NinjaKageUpdateRequest  updateRequestWithoutVillage;
        private Ninja authenticatedKage;

        @BeforeEach
        void setUp(){
            authenticatedKage = new Ninja(
                    1L, "May Uzumaki", "may@gmail.com", "May12345.", Rank.KAGE, konoha, 50000, false, Set.of(), Set.of());
            mockPrincipal = authenticatedKage::getEmail;

            ninjaToUpdate = new Ninja(
                    2L, "Violeta", "violeta@gmail.com", "Vio12345.", Rank.CHUNIN, konoha, 50, false, new HashSet<>(), new HashSet<>());

            updateRequestWithVillage = new NinjaKageUpdateRequest(
                    "Violeta Uzumaki",
                    "new.vio@gmail.com",
                    Rank.JONIN,
                    kumo.getId(),
                    true,
                    new HashSet<>()
            );

            updateRequestWithoutVillage = new NinjaKageUpdateRequest(
                    "Violeta Uzumaki",
                    "new.vio@gmail.com",
                    Rank.JONIN,
                    null,
                    true,
                    new HashSet<>()
            );
        }

        @Test
        @DisplayName("Should update ninja when a new village successfully")
        void shouldUpdateNinjaWhenVillageSuccessfully(){
            when(ninjaService.getAuthenticatedNinja(mockPrincipal)).thenReturn(authenticatedKage);
            when(ninjaService.getNinjaEntityById(2L)).thenReturn(ninjaToUpdate);
            when(villageService.getVillageEntityById(kumo.getId())).thenReturn(kumo);
            doNothing().when(ninjaService).validateKageAccess(any(Ninja.class));
            doNothing().when(ninjaService).validateEmailChange(updateRequestWithVillage.email(), ninjaToUpdate.getEmail());
            when(ninjaService.updateAsKageInternal(ninjaToUpdate.getId(), updateRequestWithVillage, kumo)).thenReturn(ninjaResponse);

            NinjaResponse result = ninjaVillageCoordinatorService.updateAsKage(ninjaToUpdate.getId(), updateRequestWithVillage, mockPrincipal);

            assertThat(result).isNotNull();
            verify(ninjaService).getAuthenticatedNinja(mockPrincipal);
            verify(ninjaService).validateKageAccess(any(Ninja.class));
            verify(ninjaService).getNinjaEntityById(ninjaToUpdate.getId());
            verify(ninjaService).validateEmailChange(updateRequestWithVillage.email(), ninjaToUpdate.getEmail());
            verify(villageService).getVillageEntityById(kumo.getId());
            verify(ninjaService).updateAsKageInternal(ninjaToUpdate.getId(), updateRequestWithVillage, kumo);
            verifyNoMoreInteractions(ninjaService, villageService);

        }

        @Test
        @DisplayName("Should update ninja successfully when villageId is not provided")
        void shouldUpdateNinjaSuccessfullyWhenVillageIdIsNotProvided(){
            when(ninjaService.getAuthenticatedNinja(mockPrincipal)).thenReturn(authenticatedKage);
            when(ninjaService.getNinjaEntityById(2L)).thenReturn(ninjaToUpdate);
            doNothing().when(ninjaService).validateKageAccess(any(Ninja.class));
            doNothing().when(ninjaService).validateEmailChange(updateRequestWithoutVillage.email(), ninjaToUpdate.getEmail());
            when(ninjaService.updateAsKageInternal(ninjaToUpdate.getId(), updateRequestWithoutVillage, null)).thenReturn(ninjaResponse);

            NinjaResponse result = ninjaVillageCoordinatorService.updateAsKage(ninjaToUpdate.getId(), updateRequestWithoutVillage, mockPrincipal);

            assertThat(result).isNotNull();
            verify(ninjaService).getAuthenticatedNinja(mockPrincipal);
            verify(ninjaService).validateKageAccess(any(Ninja.class));
            verify(ninjaService).getNinjaEntityById(ninjaToUpdate.getId());
            verify(ninjaService).validateEmailChange(updateRequestWithoutVillage.email(), ninjaToUpdate.getEmail());
            verify(villageService, never()).getVillageEntityById(anyLong());
            verify(ninjaService).updateAsKageInternal(ninjaToUpdate.getId(), updateRequestWithoutVillage, null);
            verifyNoMoreInteractions(ninjaService, villageService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when ninja to update does not exists")
        void shouldThrowExceptionWhenNinjaToUpdateDoesNotExist() {
            when(ninjaService.getAuthenticatedNinja(mockPrincipal)).thenReturn(authenticatedKage);
            when(ninjaService.getNinjaEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Ninja not found with ID 99"));

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateAsKage(99L, updateRequestWithVillage, mockPrincipal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found with ID 99");

            verify(ninjaService).getAuthenticatedNinja(mockPrincipal);
            verify(ninjaService).validateKageAccess(any(Ninja.class));
            verify(ninjaService).getNinjaEntityById(99L);
            verify(ninjaService, never()).updateAsKageInternal(anyLong(), any(), any());
            verifyNoMoreInteractions(ninjaService, villageService);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when new village does not exist")
        void shouldThrowExceptionWhenNewVillageDoesNotExist(){
            when(ninjaService.getAuthenticatedNinja(mockPrincipal)).thenReturn(authenticatedKage);
            when(ninjaService.getNinjaEntityById(2L)).thenReturn(ninjaToUpdate);
            doNothing().when(ninjaService).validateKageAccess(any(Ninja.class));
            doNothing().when(ninjaService).validateEmailChange(any(), any());
            when(villageService.getVillageEntityById(99L))
                    .thenThrow(new ResourceNotFoundException("Village not found with ID 99"));

            NinjaKageUpdateRequest invalidVillageRequest = new NinjaKageUpdateRequest(
                    "Test",
                    "test@gmail.com",
                    null,
                    99L,
                    null,
                    null
            );
            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateAsKage(2L, invalidVillageRequest, mockPrincipal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Village not found with ID 99");

            verify(ninjaService).getAuthenticatedNinja(mockPrincipal);
            verify(ninjaService).validateKageAccess(any(Ninja.class));
            verify(ninjaService).getNinjaEntityById(2L);
            verify(ninjaService).validateEmailChange(invalidVillageRequest.email(), ninjaToUpdate.getEmail());
            verify(villageService).getVillageEntityById(99L);
            verify(ninjaService, never()).updateAsKageInternal(anyLong(), any(), any());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException when new email is already in use")
        void shouldThrowExceptionWhenNewEmailIsAlreadyInUse(){
            when(ninjaService.getAuthenticatedNinja(mockPrincipal)).thenReturn(authenticatedKage);
            when(ninjaService.getNinjaEntityById(2L)).thenReturn(ninjaToUpdate);
            doNothing().when(ninjaService).validateKageAccess(any(Ninja.class));
            doThrow(new ResourceConflictException("Email already in use"))
                    .when(ninjaService).validateEmailChange(updateRequestWithVillage.email(), ninjaToUpdate.getEmail());

            assertThatThrownBy(() -> ninjaVillageCoordinatorService.updateAsKage(2L, updateRequestWithVillage, mockPrincipal))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Email already in use");

            verify(ninjaService).getAuthenticatedNinja(mockPrincipal);
            verify(ninjaService).validateKageAccess(any(Ninja.class));
            verify(ninjaService).getNinjaEntityById(2L);
            verify(ninjaService).validateEmailChange(updateRequestWithVillage.email(), ninjaToUpdate.getEmail());
            verify(villageService, never()).getVillageEntityById(anyLong());
            verify(ninjaService, never()).updateAsKageInternal(anyLong(), any(), any());
        }
    }
}
