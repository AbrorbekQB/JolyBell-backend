package uz.greenstar.jolybell.dto.order;

import lombok.Data;
import uz.greenstar.jolybell.dto.user.UserDTO;

@Data
public class CheckoutOrderDTO {
    private String orderId;
    private UserDTO user;
    private String note;
}
