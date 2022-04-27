package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.DistrictEntity;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<DistrictEntity, String> {
    Optional<DistrictEntity> findByNameIgnoreCase(String district);

    List<DistrictEntity> findAllByProvinceId(String provinceId);
}
