package uz.greenstar.jolybell.repository.spec;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.entity.*;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderListByAdminSpecification {
    public static Specification<OrderEntity> getFilteredPayment(FilterRequest request) {
        return (((root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            List<Predicate> predicateListOr = new ArrayList<>();
            if (request.getFilterData().containsKey("search") && StringUtils.hasText(request.getFilterData().get("search"))) {
                predicateList.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(OrderEntity_.ID)), "%" + request.getFilterData().get("search") + "%".toLowerCase()));

                Join<OrderEntity, UserEntity> userEntityJoin = root.join(OrderEntity_.user, JoinType.LEFT);
                predicateListOr.add(criteriaBuilder.like(criteriaBuilder.lower(userEntityJoin.get(UserEntity_.USERNAME)), "%" + request.getFilterData().get("search") + "%".toLowerCase()));
            }

            if (request.getFilterData().containsKey("state")
                    && !OrderState.NONE.equals(OrderState.find(request.getFilterData().get("state")))
                    && !request.getFilterData().get("state").equalsIgnoreCase("all"))
                predicateList.add(criteriaBuilder.equal(root.get(OrderEntity_.STATE), OrderState.find(request.getFilterData().get("state"))));

            if (request.getFilterData().containsKey("status")
                    && !request.getFilterData().get("status").equalsIgnoreCase("all")
                    && !OrderStatus.NONE.equals(OrderStatus.find(request.getFilterData().get("status"))))
                predicateList.add(criteriaBuilder.equal(root.get(OrderEntity_.STATUS), OrderStatus.find(request.getFilterData().get("status"))));

            LocalDateTime dateTo = LocalDateTime.parse(request.getFilterData().get("dateTo") + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime dateFrom = LocalDateTime.parse(request.getFilterData().get("dateFrom") + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (dateFrom.isBefore(dateTo)) {
                predicateList.add(criteriaBuilder.lessThan(root.get(OrderEntity_.CREATED_DATE_TIME), dateFrom));
                predicateList.add(criteriaBuilder.greaterThan(root.get(OrderEntity_.CREATED_DATE_TIME), dateTo));
            }
//            predicateList.add(builder.greaterThan(root.get(OrderEntity_.createTime), LocalDateTime.parse(filter.getCustomDataMap().get("dateFom"))));
//            predicateList.add(builder.lessThan(root.get(OrderEntity_.createTime), LocalDateTime.parse(filter.getCustomDataMap().get("dateTo"))));

//            predicateList.add(criteriaBuilder.or(predicateListOr.toArray(new Predicate[0])));
            query.orderBy(criteriaBuilder.desc(root.get(OrderEntity_.CREATED_DATE_TIME)));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        }));
    }
}
