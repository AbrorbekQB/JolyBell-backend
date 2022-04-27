package uz.greenstar.jolybell.dto.order;

import lombok.Data;
import uz.greenstar.jolybell.enums.OrderStatus;

@Data
public class OrderGetDTO {
    private String id;
    private OrderStatus status;
}
