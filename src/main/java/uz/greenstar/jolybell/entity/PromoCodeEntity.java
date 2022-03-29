package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table
@Entity
public class PromoCodeEntity {
    @Id
    private String id;
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createDateTime;
    private Double percent;
}
