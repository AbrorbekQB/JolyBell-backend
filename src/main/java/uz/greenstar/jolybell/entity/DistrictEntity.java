package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Table
@Entity(name = "district")
public class DistrictEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String name;
    private String provinceId;
}
