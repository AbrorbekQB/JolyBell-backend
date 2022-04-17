package uz.greenstar.jolybell.repository.spec;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.entity.ProductEntity;
import uz.greenstar.jolybell.entity.ProductEntity_;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductListByAdminSpecification {

    public static Specification<ProductEntity> getFilteredPayment(FilterRequest request) {
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (request.getFilterData().containsKey("search") && StringUtils.hasText(request.getFilterData().get("search")))
                predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(ProductEntity_.NAME)), "%" + request.getFilterData().get("search") + "%".toLowerCase()));

            query.orderBy(criteriaBuilder.desc(root.get(ProductEntity_.ACTIVE)))
                    .orderBy(criteriaBuilder.desc(root.get(ProductEntity_.NAME)));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }));
    }
}
