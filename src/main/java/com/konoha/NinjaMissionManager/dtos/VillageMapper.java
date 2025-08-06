package com.konoha.NinjaMissionManager.dtos;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;

public interface VillageMapper {
    Village dtoToEntity(VillageRequest dto, Ninja ninja);
    VillageResponse entityToDto(Village village);
}
