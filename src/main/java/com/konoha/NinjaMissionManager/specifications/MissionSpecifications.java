package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class MissionSpecifications {
    public static Specification<Mission> hasDifficulty(MissionDifficulty difficulty){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("difficulty"), difficulty);
    }

    public static Specification<Mission> hasStatus(Status status){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Mission> isAssignedToNinja(Long ninjaId){
        return (root, query, criteriaBuilder) ->{
            Join assignedNinjas = root.join("assignedNinjas");
            return criteriaBuilder.equal(assignedNinjas.get("id"), ninjaId);
        };
    }
}