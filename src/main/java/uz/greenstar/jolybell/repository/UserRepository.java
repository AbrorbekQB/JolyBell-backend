package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
}
