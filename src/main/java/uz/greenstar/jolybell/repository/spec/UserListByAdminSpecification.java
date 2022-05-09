package uz.greenstar.jolybell.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.entity.CategoryEntity_;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.entity.UserEntity_;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserListByAdminSpecification {

    public static Specification<UserEntity> getFilteredPayments(FilterRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (request.getFilterData().containsKey("search") && StringUtils.hasText(request.getFilterData().get("search")))
                predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(UserEntity_.USERNAME)), "%" + request.getFilterData().get("search") + "%".toLowerCase()));

            query.orderBy(criteriaBuilder.desc(root.get(UserEntity_.CREATE_TIME))).distinct(true);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        });
    }
}
