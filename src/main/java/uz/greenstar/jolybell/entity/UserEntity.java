package uz.greenstar.jolybell.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
@Table
@Entity(name = "users")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private Boolean active = Boolean.FALSE;
    private String username;
    private String firstname = "";
    private String lastname = "";
    private String patronymic = "";
    private String password;
    private String phoneNumber = "";
    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime lastUpdateTime = LocalDateTime.now();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "userId")},
            inverseJoinColumns = {@JoinColumn(name = "roleId")})
    private Set<RoleEntity> roleList = new HashSet<>();
}
