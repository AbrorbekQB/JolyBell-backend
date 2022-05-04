package uz.greenstar.jolybell.dto.order;

import lombok.Data;

@Data
public class ChoicePaymentDTO {
    private String orderId;
    private String type;
}
