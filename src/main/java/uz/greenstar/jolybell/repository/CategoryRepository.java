package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import uz.greenstar.jolybell.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String>, JpaSpecificationExecutor<CategoryEntity> {
    Optional<CategoryEntity> findByUrl(String url);

    @Query(value = "SELECT * FROM category ORDER BY active DESC, name DESC", nativeQuery = true)
    List<CategoryEntity> findAllOrderByActive();

    List<CategoryEntity> findAllByActiveTrueOrderByNameDesc();
}
