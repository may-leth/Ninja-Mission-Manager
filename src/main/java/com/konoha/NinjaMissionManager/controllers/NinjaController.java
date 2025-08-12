package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.services.NinjaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ninjas")
@RequiredArgsConstructor
public class NinjaController {
    private final NinjaService ninjaService;

    @GetMapping
    public ResponseEntity<List<NinjaResponse>> getAllNinjas(
            @RequestParam(required = false) Optional<Rank> rank,
            @RequestParam(required = false) Optional<Long> villageId,
            @RequestParam(required = false) Optional<Boolean> isAnbu
    ) {
       List<NinjaResponse> ninjas = ninjaService.getAllNinjas(rank, villageId, isAnbu);
       return ResponseEntity.ok(ninjas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NinjaResponse> getNinjaById(@PathVariable Long id){
        NinjaResponse ninja = ninjaService.getNinjaById(id);
        return ResponseEntity.ok(ninja);
    }
}
