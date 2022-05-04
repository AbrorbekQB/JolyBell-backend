package uz.greenstar.jolybell.dto.payment;

import lombok.Data;

@Data
public class PaySysPayDTO {
    private String orderId;
    private String cardHolderName;
    private String cardNumber;
    private String cardExpire;
    private String cardCvc;
}
