package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Table
@Entity(name = "roles")
public class RoleEntity implements Serializable {
    private static final long serialVersionUID = -1582007018827587161L;

    @Id
    private String id = UUID.randomUUID().toString();

    @Column(name = "name")
    private String name;
}
