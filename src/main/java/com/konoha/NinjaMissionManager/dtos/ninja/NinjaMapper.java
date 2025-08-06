package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import com.konoha.NinjaMissionManager.models.Village;

import java.util.Set;

public interface NinjaMapper {
    Ninja dtoToEntity(NinjaRegisterRequest dto, Village village, Rank rank, Integer missionsCompletedCount, Boolean isAnbu, Set<Role> roles);
    Ninja dtoToEntity(KageCreateNinjaRequest dto, Village village);
    NinjaResponse entityToDto(Ninja ninja);
}