package com.konoha.NinjaMissionManager.repositories;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Village;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VillageRepository extends JpaRepository<Village, Long>, JpaSpecificationExecutor<Village> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByKageId(Long kageId);
    boolean existsByKageAndIdNot(Ninja kage, Long villageId);
}