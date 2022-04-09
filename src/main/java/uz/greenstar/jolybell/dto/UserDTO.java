package uz.greenstar.jolybell.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private boolean active;
    private String username;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String password;
    private String phoneNumber;
    private String createDateTime;
}
