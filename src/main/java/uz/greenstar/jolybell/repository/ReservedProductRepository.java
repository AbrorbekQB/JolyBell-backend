package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.ReservedProductEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservedProductRepository extends JpaRepository<ReservedProductEntity, String> {
    List<ReservedProductEntity> findTop10ByReturnedFalseAndCreateDateBefore(LocalDateTime localDateTime);

    List<ReservedProductEntity> findAllByOrderIdAndReturnedFalse(String orderId);
}
