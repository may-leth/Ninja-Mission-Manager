package com.konoha.NinjaMissionManager.repositories;

import com.konoha.NinjaMissionManager.models.Ninja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NinjaRepository extends JpaRepository<Ninja, Long>, JpaSpecificationExecutor<Ninja> {
    Optional<Ninja> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Ninja> findByVillageId(Long villageId);
}
