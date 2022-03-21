package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
