package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.ActivationCodeEntity;

public interface ActivationCodeRepository extends JpaRepository<ActivationCodeEntity, String> {
}
