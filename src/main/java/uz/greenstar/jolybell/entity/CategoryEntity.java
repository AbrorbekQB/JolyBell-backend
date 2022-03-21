package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Table
@Entity(name = "category")
public class CategoryEntity {
    @Id
    private String id;
    private String name;
    private String url;

    @OneToMany(fetch = FetchType.LAZY)
    List<ProductEntity> productList;
}
