package com.may.Ninja.Mission.Manager.controllers;

import com.may.Ninja.Mission.Manager.dtos.MissionRequest;
import com.may.Ninja.Mission.Manager.dtos.MissionResponse;
import com.may.Ninja.Mission.Manager.services.MissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/missions")
public class MissionController {
    private final MissionService missionService;

    @GetMapping
    public ResponseEntity<List<MissionResponse>> getAllMissions(){
        List<MissionResponse> missions = missionService.getAllMissions();
        return ResponseEntity.ok(missions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissionResponse> getMissionById(@PathVariable Long id){
        MissionResponse mission = missionService.getMissionById(id);
        return ResponseEntity.ok(mission);
    }

    @PostMapping
    public ResponseEntity<MissionResponse> addMission(@Valid @RequestBody MissionRequest request){
        MissionResponse response = missionService.addMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
