package com.konoha.NinjaMissionManager.specifications;

import com.konoha.NinjaMissionManager.models.Village;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

@NoArgsConstructor
public class VillageSpecificationBuilder {
    private Specification<Village> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    public static VillageSpecificationBuilder builder() {
        return new VillageSpecificationBuilder();
    }

    public VillageSpecificationBuilder kageName(Optional<String> kageName){
        kageName.ifPresent(name -> specification = specification.and(VillageSpecifications.hasKageName(name)));
        return this;
    }

    public Specification<Village> build(){
        return this.specification;
    }
}
