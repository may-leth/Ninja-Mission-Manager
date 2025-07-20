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

    @PutMapping("/{id}")
    public ResponseEntity<MissionResponse> updateMission(@PathVariable Long id, @Valid @RequestBody MissionRequest request){
        MissionResponse updatedMission = missionService.updateMission(id, request);
        return ResponseEntity.ok(updatedMission);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MissionResponse> deleteMission(@PathVariable Long id){
        MissionResponse deletedMission = missionService.deleteMission(id);
        return ResponseEntity.ok(deletedMission);
    }
}
