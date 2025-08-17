package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Village;
import org.springframework.data.jpa.domain.Specification;

public class VillageSpecifications {
    public static Specification<Village> hasKageName(String kageName){
        return ((root, query, criteriaBuilder)
                -> criteriaBuilder.equal(root.get("kage"), kageName));
    }
}
