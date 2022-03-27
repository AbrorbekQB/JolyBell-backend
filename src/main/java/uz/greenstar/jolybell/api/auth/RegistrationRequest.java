package uz.greenstar.jolybell.api.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
    private String username;
    private String firstname;
    private String lastname;
    private String patronym;
    private String password;
    private String phoneNumber;
}
