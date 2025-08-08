package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import org.springframework.data.jpa.domain.Specification;

public class NinjaSpecifications {
    public static Specification<Ninja> hasRank(Rank rank){
        return (root, query,criteriaBuilder)
                -> criteriaBuilder.equal(root.get("rank"), rank);
    }

    public static Specification<Ninja> inVillageId(Long villageId){
        return (root, query,criteriaBuilder)
                -> criteriaBuilder.equal(root.get("village").get("id"), villageId);
    }

    public static Specification<Ninja> isAnbu(Boolean isAnbu){
        return (root, query,criteriaBuilder)
                -> criteriaBuilder.equal(root.get("isAnbu"), isAnbu);
    }
}
