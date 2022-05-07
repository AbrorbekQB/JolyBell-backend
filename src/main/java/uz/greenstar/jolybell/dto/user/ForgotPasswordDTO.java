package uz.greenstar.jolybell.dto.user;

import lombok.Data;

@Data
public class ForgotPasswordDTO {
    private String confirmCode;
    private String password;
}
