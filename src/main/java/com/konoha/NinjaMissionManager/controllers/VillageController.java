package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;
import com.konoha.NinjaMissionManager.services.VillageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/villages")
@RequiredArgsConstructor
public class VillageController {
    private final VillageService villageService;

    @GetMapping
    public ResponseEntity<List<VillageResponse>> getAllVillages(
            @RequestParam(required = false)Optional<String> kageName
            ) {
        List<VillageResponse> villages = villageService.getAllVillages(kageName);
        return ResponseEntity.ok(villages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VillageResponse> getVillageById(@PathVariable Long id){
        VillageResponse village = villageService.getVillageById(id);
        return ResponseEntity.ok(village);
    }
}
