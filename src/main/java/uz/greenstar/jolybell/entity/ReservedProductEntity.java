package uz.greenstar.jolybell.entity;

import lombok.Data;
import uz.greenstar.jolybell.enums.ReservedProductStatus;

import javax.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private ReservedProductStatus status = ReservedProductStatus.RESERVED;
    private String orderId;
    private String productCountId;
    private LocalDateTime createDate = LocalDateTime.now();
    private LocalDateTime lastUpdateDate = LocalDateTime.now();
}
