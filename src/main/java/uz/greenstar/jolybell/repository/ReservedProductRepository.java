package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.ReservedProductEntity;
import uz.greenstar.jolybell.enums.ReservedProductStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservedProductRepository extends JpaRepository<ReservedProductEntity, String> {
    List<ReservedProductEntity> findTop10ByStatusAndCreateDateBefore(ReservedProductStatus status, LocalDateTime localDateTime);

    List<ReservedProductEntity> findAllByOrderIdAndStatus(String orderId, ReservedProductStatus status);
}
