package uz.greenstar.jolybell.api.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtDTO {
    private String userId;
    private String username;
    private List<String> role;
}