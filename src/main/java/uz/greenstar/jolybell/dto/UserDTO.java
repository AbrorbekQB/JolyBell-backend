package uz.greenstar.jolybell.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uz.greenstar.jolybell.enums.Role;

@Data
@RequiredArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String patronym;
    private String password;
    private String phoneNumber;
    private Role role;
}
