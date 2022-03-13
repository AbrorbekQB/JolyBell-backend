package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table
@Entity(name = "category")
public class CategoryEntity {
    @Id
    private String id;
    private String name;
}
