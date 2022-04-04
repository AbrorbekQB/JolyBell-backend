package uz.greenstar.jolybell.dto;

import lombok.Data;
import uz.greenstar.jolybell.api.order.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class OrderDTO {
    private String id;
    private List<OrderItem> orderItems = new ArrayList<>();
    private BigDecimal totalAmount;
}
