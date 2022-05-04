package uz.greenstar.jolybell.api.payment;

import lombok.Data;

import java.util.Map;

@Data
public class PaySysErrorMessage {
    private Integer code;
    private String message;
    private Map<String, Object> data;
}
