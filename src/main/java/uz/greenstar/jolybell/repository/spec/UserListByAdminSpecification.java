package uz.greenstar.jolybell.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.entity.UserEntity;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserListByAdminSpecification {

    public static Specification<UserEntity> getFilteredPayments(FilterRequest request) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate>predicateList = new ArrayList<>();

//            if (StringUtils.hasText(request.getFilterData().get("email")))
//                predicateList.add(criteriaBuilder.equal(root.get(UserEntity_.)))
            query.distinct(true);
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        });
    }
}
