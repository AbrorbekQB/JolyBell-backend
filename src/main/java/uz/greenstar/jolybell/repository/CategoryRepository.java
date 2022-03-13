package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {
}
