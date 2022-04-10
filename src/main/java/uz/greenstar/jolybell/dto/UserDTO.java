package uz.greenstar.jolybell.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String id;
    private boolean active;
    private List<String> roles;
    private String username;
    private String firstname;
    private String lastname;
    private String patronymic;
    private String password;
    private String phoneNumber;
    private String createDateTime;
}
