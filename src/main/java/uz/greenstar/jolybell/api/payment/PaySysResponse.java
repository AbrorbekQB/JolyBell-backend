package uz.greenstar.jolybell.api.payment;

import lombok.Data;

import java.util.Map;

@Data
public class PaySysResponse {
    private String id;
    private String mxId;
    private PaySysErrorMessage error;
    private Map<String, Object> result;
    private boolean successfullyFinished = false;
}
