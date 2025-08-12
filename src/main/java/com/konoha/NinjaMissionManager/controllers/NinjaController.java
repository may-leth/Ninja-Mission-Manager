package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.services.NinjaServices;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ninjas")
@RequiredArgsConstructor
public class NinjaController {
    private final NinjaServices ninjaServices;

    @GetMapping
    public ResponseEntity<List<NinjaResponse>> getAllNinjas(
            @RequestParam(required = false) Optional<Rank> rank,
            @RequestParam(required = false) Optional<Long> villageId,
            @RequestParam(required = false) Optional<Boolean> isAnbu
    ) {
       List<NinjaResponse> ninjas = ninjaServices.getAllNinjas(rank, villageId, isAnbu);
       return ResponseEntity.ok(ninjas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NinjaResponse> getNinjaById(@PathVariable Long id){
        NinjaResponse ninja = ninjaServices.getNinjaById(id);
        return ResponseEntity.ok(ninja);
    }
}
