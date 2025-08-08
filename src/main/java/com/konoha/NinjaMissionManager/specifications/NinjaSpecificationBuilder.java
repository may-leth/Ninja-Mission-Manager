package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@NoArgsConstructor
public class NinjaSpecificationBuilder {
    private Specification<Ninja> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    public static NinjaSpecificationBuilder builder(){
        return new NinjaSpecificationBuilder();
    }

    public NinjaSpecificationBuilder rank(Optional<Rank> rank){
        rank.ifPresent(r -> specification = specification.and(NinjaSpecifications.hasRank(r)));
        return this;
    }

    public NinjaSpecificationBuilder village(Optional<Long> villageId){
        villageId.ifPresent(id -> specification = specification.and(NinjaSpecifications.inVillageId(id)));
        return this;
    }

    public NinjaSpecificationBuilder isAnbu(Optional<Boolean> isAnbu){
        isAnbu.ifPresent(anbu -> specification = specification.and(NinjaSpecifications.isAnbu()));
        return this;
    }

    public Specification<Ninja> build(){
        return this.specification;
    }
}
