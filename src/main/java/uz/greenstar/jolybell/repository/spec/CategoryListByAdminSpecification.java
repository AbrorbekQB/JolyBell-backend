package uz.greenstar.jolybell.repository.spec;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.entity.CategoryEntity;
import uz.greenstar.jolybell.entity.CategoryEntity_;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CategoryListByAdminSpecification {

    public static Specification<CategoryEntity> getFilteredPayment(FilterRequest request) {
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (request.getFilterData().containsKey("search") && StringUtils.hasText(request.getFilterData().get("search")))
                predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(CategoryEntity_.NAME)), "%" + request.getFilterData().get("search") + "%".toLowerCase()));

            query.orderBy(criteriaBuilder.desc(root.get(CategoryEntity_.ACTIVE)))
                    .orderBy(criteriaBuilder.desc(root.get(CategoryEntity_.NAME)));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }));
    }
}
