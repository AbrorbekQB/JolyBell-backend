package uz.greenstar.jolybell.api.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderData {
    private String id;
    private String createdDate;
    private List<OrderItem> orderItems;
    private OrderState state;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String email;
    private String address;
    private String note;
    private String province;
    private String phoneNumber;
}
