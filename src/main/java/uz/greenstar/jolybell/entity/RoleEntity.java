package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Data
@Table
@Entity(name = "roles")
public class RoleEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String role;
}