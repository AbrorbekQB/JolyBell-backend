package uz.greenstar.jolybell.api.order;

import lombok.Data;

@Data
public class OrderItemRemove {
    private String orderId;
    private String productId;
    private String orderItemId;
}
