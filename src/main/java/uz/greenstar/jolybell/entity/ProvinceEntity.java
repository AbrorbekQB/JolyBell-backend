package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Data
@Table
@Entity(name = "province")
public class ProvinceEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
}
