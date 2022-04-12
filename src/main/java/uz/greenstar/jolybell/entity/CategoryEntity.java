package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table
@Entity(name = "category")
public class CategoryEntity {
    @Id
    private String id;
    private String name;

    @Column(name = "url", unique = true)
    private String url;
    private Boolean active = Boolean.FALSE;
    private LocalDateTime createDateTime = LocalDateTime.now();
    private LocalDateTime lastUpdateTime = LocalDateTime.now();

    @ManyToOne
    private UserEntity user;


}
