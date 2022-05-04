package uz.greenstar.jolybell.entity;

import lombok.Data;
import uz.greenstar.jolybell.enums.PaySysStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table
@Entity(name = "paysys")
public class PaySysEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private BigDecimal totalAmount;
    private String transactionId;
    private String orderId;
    @Enumerated(EnumType.STRING)
    private PaySysStatus status = PaySysStatus.INPUT;
    private LocalDateTime createdDateTime = LocalDateTime.now();
    private LocalDateTime lastUpdateTime = LocalDateTime.now();
}
