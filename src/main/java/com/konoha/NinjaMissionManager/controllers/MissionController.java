package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import com.konoha.NinjaMissionManager.services.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
