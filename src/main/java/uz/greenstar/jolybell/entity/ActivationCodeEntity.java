package uz.greenstar.jolybell.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(name = "activation_code")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivationCodeEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String userId;
    private LocalDateTime createTime = LocalDateTime.now();
}
