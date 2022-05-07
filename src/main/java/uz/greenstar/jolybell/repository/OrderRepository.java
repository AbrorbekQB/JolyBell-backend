package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.PromoCodeEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {
    Optional<OrderEntity> findByIdAndStatus(String id, OrderStatus status);

    Optional<OrderEntity> findByUserAndPromoCode(UserEntity user, PromoCodeEntity promoCode);

    Optional<OrderEntity> findByIdAndStatusNotIn(String orderId, List<OrderStatus> list);

    List<OrderEntity> findAllByUserAndStateIsNotOrderByCreatedDateTimeDesc(UserEntity user, OrderState state);
}
