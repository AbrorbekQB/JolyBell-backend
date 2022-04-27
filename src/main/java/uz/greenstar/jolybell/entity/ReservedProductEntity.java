package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@Entity(name = "product_reserved")
public class ReservedProductEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private Long count = 0L;
    private String size = "all";
    private boolean returned = false;
    private String orderId;
    private String productCountId;
    private LocalDateTime createDate = LocalDateTime.now();
    private LocalDateTime lastUpdateDate = LocalDateTime.now();
}
