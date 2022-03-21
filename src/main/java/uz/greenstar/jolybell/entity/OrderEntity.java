package uz.greenstar.jolybell.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Table
@Entity(name = "order")
public class OrderEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String productId;
    private long count;
    private BigDecimal cost;
}
