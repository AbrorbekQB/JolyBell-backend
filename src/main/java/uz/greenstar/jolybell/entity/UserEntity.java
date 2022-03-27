package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Table
@Entity(name = "users")
public class UserEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String username;
    private String firstname;
    private String lastname;
    private String patronym;
    private String password;
    private String phoneNumber;
    private LocalDateTime createTime;
    private LocalDateTime lastUpdateTime;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<RoleEntity> roles = new HashSet<>();
}
