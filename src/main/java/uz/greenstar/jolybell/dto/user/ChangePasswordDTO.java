package uz.greenstar.jolybell.dto.user;

import lombok.Getter;

@Getter
public class ChangePasswordDTO {
    private String password;
    private String confirmPassword;
}
