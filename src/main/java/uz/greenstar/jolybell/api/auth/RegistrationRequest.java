package uz.greenstar.jolybell.api.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RegistrationRequest {
    private String username;
    private String password;
}
