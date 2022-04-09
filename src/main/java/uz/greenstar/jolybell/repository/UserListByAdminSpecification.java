package uz.greenstar.jolybell.repository;

import org.hibernate.mapping.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.pagination.PaginationRequest;
import uz.greenstar.jolybell.entity.UserEntity;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserListByAdminSpecification {

    public static Specification<UserEntity> getFilteredPayments(PaginationRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate>predicateList = new ArrayList<>();

//            if (StringUtils.hasText(request.getFilterData().get("email")))
//                predicateList.add(criteriaBuilder.equal(root.get(UserEntity_.)))
            query.distinct(true);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        });
    }
}
