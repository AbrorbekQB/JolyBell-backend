package uz.greenstar.jolybell.api.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private String productId;
    private int count;
    private BigDecimal cost = BigDecimal.ZERO;
    private String size;
}
