package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.PromoCodeEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.enums.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    Optional<OrderEntity> findByIdAndStatus(String id, OrderStatus status);

    Optional<OrderEntity> findByUserAndPromoCode(UserEntity user, PromoCodeEntity promoCode);
}
