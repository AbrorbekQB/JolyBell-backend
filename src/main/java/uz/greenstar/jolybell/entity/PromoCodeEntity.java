package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@Entity(name = "promocode")
public class PromoCodeEntity {
    @Id
    private String id= UUID.randomUUID().toString();
    private String code;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createDateTime;
    private LocalDateTime lastUpdateTime;
    private Double percent;
}
