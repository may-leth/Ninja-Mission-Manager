package com.konoha.NinjaMissionManager.repositories;

import com.konoha.NinjaMissionManager.models.Ninja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NinjaRepository extends JpaRepository<Ninja, Long>, JpaSpecificationExecutor<Ninja> {
}
