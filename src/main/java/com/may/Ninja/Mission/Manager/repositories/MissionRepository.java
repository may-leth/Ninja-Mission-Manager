package com.may.Ninja.Mission.Manager.repositories;

import com.may.Ninja.Mission.Manager.models.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
}
