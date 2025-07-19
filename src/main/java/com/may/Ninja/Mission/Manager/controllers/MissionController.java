package com.may.Ninja.Mission.Manager.controllers;

import com.may.Ninja.Mission.Manager.dtos.MissionResponse;
import com.may.Ninja.Mission.Manager.services.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
