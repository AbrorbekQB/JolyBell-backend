package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
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
    private Instant createTime;
    private Instant lastUpdateTime;
}
