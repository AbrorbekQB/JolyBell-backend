package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.greenstar.jolybell.entity.ProductCountEntity;
import uz.greenstar.jolybell.entity.ProductEntity;

import java.util.List;

public interface ProductCountRepository extends JpaRepository<ProductCountEntity, String> {
    List<ProductCountEntity> findAllByProduct(ProductEntity product);
}
