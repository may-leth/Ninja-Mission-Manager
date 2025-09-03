package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaEmailInfo;

import java.util.List;

public interface EmailSender {
    void sendNinjaWelcomeEmail(String toNinja, String ninjaName, String village);
    void sendMissionAssignmentEmail(List<NinjaEmailInfo> ninjaTeam, String missionTitle, String missionDescription, String missionRank);
}