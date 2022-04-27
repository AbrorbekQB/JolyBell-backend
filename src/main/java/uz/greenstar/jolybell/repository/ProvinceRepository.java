package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.ProvinceEntity;

import java.util.Optional;

public interface ProvinceRepository extends JpaRepository<ProvinceEntity, String> {

    Optional<ProvinceEntity> findByNameIgnoreCase(String s);
}
