package uz.greenstar.jolybell.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import uz.greenstar.jolybell.entity.ProductEntity;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String>, JpaSpecificationExecutor<ProductEntity> {
    @Query(value = "SELECT * FROM product where category_id = (SELECT id FROM category WHERE url=:url)", nativeQuery = true)
    List<ProductEntity> findAllByCategoryUrl(String url);
}
