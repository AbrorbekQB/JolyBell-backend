package uz.greenstar.jolybell.dto.order;

import lombok.Data;
import uz.greenstar.jolybell.api.order.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDTO {
    private String id;
    private List<OrderItem> orderItems = new ArrayList<>();
    private BigDecimal totalAmount;
    private Boolean fullReserved;
    private Long orderLifeTime;
}
