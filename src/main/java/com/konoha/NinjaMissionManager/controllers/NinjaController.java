package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.services.NinjaService;
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
@RequestMapping("/ninjas")
@RequiredArgsConstructor
@Tag(name = "Ninjas", description = "Endpoints para la gestión de Ninjas")
public class NinjaController {
    private final NinjaService ninjaService;

    @Operation(summary = "Obtener todos los ninjas con filtros opcionales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de ninjas recuperada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<NinjaResponse>> getAllNinjas(
            @Parameter(description = "Filtro opcional por rango del ninja")
            @RequestParam(required = false) Optional<Rank> rank,
            @Parameter(description = "Filtro opcional por ID de la aldea del ninja")
            @RequestParam(required = false) Optional<Long> villageId,
            @Parameter(description = "Filtro opcional para saber si un ninja es ANBU")
            @RequestParam(required = false) Optional<Boolean> isAnbu
    ) {
       List<NinjaResponse> ninjas = ninjaService.getAllNinjas(rank, villageId, isAnbu);
       return ResponseEntity.ok(ninjas);
    }

    @Operation(summary = "Obtener un ninja por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ninja recuperado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ninja no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NinjaResponse> getNinjaById(
            @Parameter(description = "ID del ninja a buscar")
            @PathVariable Long id
    ){
        NinjaResponse ninja = ninjaService.getNinjaById(id);
        return ResponseEntity.ok(ninja);
    }
}
