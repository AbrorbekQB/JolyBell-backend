package uz.greenstar.jolybell.api.auth;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String token;
    private String userId;
    private String username;
    private List<String> role;
}
