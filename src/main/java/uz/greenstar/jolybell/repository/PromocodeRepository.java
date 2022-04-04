package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.PromoCodeEntity;

import java.util.Optional;

public interface PromocodeRepository extends JpaRepository<PromoCodeEntity, String> {
    Optional<PromoCodeEntity> findByCode(String code);
}
