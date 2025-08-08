package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapperImpl;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NinjaServices {
    private final NinjaRepository ninjaRepository;
    private final NinjaMapperImpl ninjaMapper;

    public List<NinjaResponse> getAllNinjas(Optional<Rank> rank, Optional<Long> villageId, Optional<Boolean> isAnbu){

        return ninjaRepository.findAll()
                .stream()
                .map(ninjaMapper::entityToDto)
                .collect(Collectors.toList());
    }


}
