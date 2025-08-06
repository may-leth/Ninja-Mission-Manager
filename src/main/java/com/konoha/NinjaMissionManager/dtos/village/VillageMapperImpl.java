package com.konoha.NinjaMissionManager.dtos.village;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import org.springframework.stereotype.Component;

@Component
public class VillageMapperImpl implements VillageMapper {

    @Override
    public Village dtoToEntity(VillageRequest dto, Ninja kageNinja) {
        return Village.builder()
                .name(dto.name())
                .kage(kageNinja)
                .build();
    }

    @Override
    public VillageResponse entityToDto(Village village) {
        String kageName = village.getKage().getName();
        return new VillageResponse(
                village.getId(),
                village.getName(),
                kageName
        );
    }
}
