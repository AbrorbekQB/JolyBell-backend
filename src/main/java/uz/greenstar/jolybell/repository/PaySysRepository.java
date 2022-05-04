package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.PaySysEntity;

public interface PaySysRepository extends JpaRepository<PaySysEntity, String> {
}
