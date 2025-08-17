package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.services.VillageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/villages")
@RequiredArgsConstructor
@Tag(name = "Villages", description = "Endpoints para la gestión de Aldeas")
public class VillageController {
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
        VillageResponse village = villageService.getVillageById(id);
        return ResponseEntity.ok(village);
    }
}
