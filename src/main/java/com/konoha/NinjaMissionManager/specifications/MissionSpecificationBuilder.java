package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@NoArgsConstructor
public class MissionSpecificationBuilder {
    private Specification<Mission> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    public static MissionSpecificationBuilder builder(){
        return new MissionSpecificationBuilder();
    }

    public MissionSpecificationBuilder difficulty(Optional<MissionDifficulty> difficulty){
        difficulty.ifPresent(d -> specification = specification.and(MissionSpecifications.hasDifficulty(d)));
        return this;
    }

    public MissionSpecificationBuilder status(Optional<Status> status){
        status.ifPresent(s -> specification = specification.and(MissionSpecifications.hasStatus(s)));
        return this;
    }

    public MissionSpecificationBuilder assignedToNinja(Optional<Long> ninjaId){
        ninjaId.ifPresent(id -> specification = specification.and(MissionSpecifications.isAssignedToNinja(id)));
        return this;
    }

    public Specification<Mission> build() {
        return this.specification;
    }
}
