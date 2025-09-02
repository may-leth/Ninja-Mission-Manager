package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.mission.MissionCreateRequest;
import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionUpdateRequest;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import com.konoha.NinjaMissionManager.services.MissionService;
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
@RequestMapping("/missions")
@RequiredArgsConstructor
@Tag(name = "Missions", description = "Endpoints para la gestion de Misiones")
public class MissionController {
    private final MissionService missionService;

    @Operation(summary = "Obtener todas las misiones con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de misiones recuperada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no es un Kage"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<MissionSummaryResponse>> getAllMissions(
            @Parameter(description = "Filtro opcional por dificultad de la misión")
            @RequestParam(required = false) Optional<MissionDifficulty> difficulty,
            @Parameter(description = "Filtro opcional por estado de la misión")
            @RequestParam(required = false) Optional<Status> status,
            @Parameter(description = "Filtro opcional para listar las misiones de un ninja específico por Id (solo Kage)")
            @RequestParam(required = false) Optional<Long> assignToNinjaId,
            Principal principal
    ) {
        List<MissionSummaryResponse> missions = missionService.getAllMissions(difficulty, status, assignToNinjaId, principal);
        return ResponseEntity.ok(missions);
    }

    @Operation(summary = "Obtener una misión por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Misión recuperada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no es Kage o no está asignado a la misión"),
            @ApiResponse(responseCode = "404", description = "Misión no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MissionResponse> getMissionById(
            @Parameter(description = "ID de la misión a buscar")
            @PathVariable Long id,
            Principal principal
    ){
        MissionResponse mission = missionService.getMissionById(id, principal);
        return ResponseEntity.ok(mission);
    }

    @Operation(summary = "Crear una nueva misión (solo para Kage)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Misión creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no es un Kage"),
            @ApiResponse(responseCode = "404", description = "Uno o más ninjas asignados no fueron encontrados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    @PreAuthorize("hasRole('KAGE')")
    public ResponseEntity<MissionResponse> createMission(
            @RequestBody @Valid MissionCreateRequest request,
            Principal principal
    ){
        MissionResponse newMission = missionService.createMission(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMission);
    }

    @Operation(summary = "Actualizar una misión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Misión actualizada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no tiene permisos para actualizar esta misión"),
            @ApiResponse(responseCode = "404", description = "Misión o ninjas asignados no encontrados"),
            @ApiResponse(responseCode = "409", description = "Conflicto de recursos, el título ya existe")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NINJA_USER') or hasRole('KAGE')")
    public ResponseEntity<MissionResponse> updateMission(
            @Parameter(description = "ID de la misión a actualizar")
            @PathVariable Long id,
            @RequestBody @Valid MissionUpdateRequest request,
            Principal principal
    ) {
        MissionResponse updatedMission = missionService.updateMission(id, request, principal);
        return ResponseEntity.ok(updatedMission);
    }

    @Operation(summary = "Eliminar una misión (solo para Kage)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Misión eliminada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado, el usuario no es un Kage"),
            @ApiResponse(responseCode = "404", description = "Misión no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('KAGE')")
    public ResponseEntity<Void> deleteMission(
            @Parameter(description = "ID de la misión a eliminar")
            @PathVariable Long id,
            Principal principal
    ) {
        missionService.deleteMission(id, principal);
        return ResponseEntity.noContent().build();
    }
}
