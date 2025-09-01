package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.services.NinjaService;
import com.konoha.NinjaMissionManager.services.NinjaVillageCoordinatorService;
import com.konoha.NinjaMissionManager.services.VillageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/villages")
@RequiredArgsConstructor
@Tag(name = "Villages", description = "Endpoints para la gestión de Aldeas")
public class VillageController {
    private final NinjaVillageCoordinatorService ninjaVillageCoordinatorService;
    private final VillageService villageService;

    @Operation(summary = "Obtener todas las aldeas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aldeas recuperada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<VillageResponse>> getAllVillages(
            @Parameter(description = "Filtro opcional por nombre del Kage")
            @RequestParam(required = false)Optional<String> kageName
            ) {
        List<VillageResponse> villages = villageService.getAllVillages(kageName);
        return ResponseEntity.ok(villages);
    }

    @Operation(summary = "Obtener una aldea por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aldea recuperada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Aldea no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VillageResponse> getVillageById(
            @Parameter(description = "ID de la aldea a buscar")
            @PathVariable Long id){
        VillageResponse village = villageService.getVillageResponseById(id);
        return ResponseEntity.ok(village);
    }

    @Operation(summary = "Añadir una aldea")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aldea creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (error de validación)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Kage no encontrado con el ID proporcionado")
    })
    @PreAuthorize("hasRole('KAGE')")
    @PostMapping
    public ResponseEntity<VillageResponse> createVillage(
            @Valid @RequestBody VillageRequest villageRequest) {
        VillageResponse response = ninjaVillageCoordinatorService.createVillage(villageRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar una aldea por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aldea actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (error de validación)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Aldea o Kage no encontrado con el ID proporcionado"),
            @ApiResponse(responseCode = "409", description = "Conflicto (nombre de aldea o Kage ya en uso)")
    })
    @PreAuthorize("hasRole('KAGE')")
    @PutMapping("/{id}")
    public ResponseEntity<VillageResponse> updateVillage(
            @Parameter(description = "ID de la aldea a actualizar")
            @PathVariable Long id,
            @Valid @RequestBody VillageUpdateRequest villageUpdateRequest) {
        VillageResponse response = ninjaVillageCoordinatorService.updateVillage(id, villageUpdateRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Eliminar una aldea por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aldea eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Aldea no encontrada")
    })
    @PreAuthorize("hasRole('KAGE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVillage(
            @Parameter(description = "ID de la aldea a eliminar")
            @PathVariable Long id) {
        ninjaVillageCoordinatorService.deleteVillage(id);
        return ResponseEntity.noContent().build();
    }
}
