package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.validation.ValidatorError;
import uz.greenstar.jolybell.dto.user.ChangePasswordDTO;
import uz.greenstar.jolybell.dto.user.UserDTO;
import uz.greenstar.jolybell.exception.ValidationException;
import uz.greenstar.jolybell.service.UserService;
import uz.greenstar.jolybell.utils.validattion.UserValidation;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final UserValidation userValidation;

    @GetMapping("/get")
    public ResponseEntity<UserDTO> get() {
        return ResponseEntity.ok(userService.get());
    }

    @PostMapping(path = "/update")
    public void update(@RequestBody UserDTO userDTO) {
        ValidatorError validatorError = userValidation.validateUserUpdate(userDTO);
        if (!validatorError.getErrors().isEmpty())
            throw new ValidationException(validatorError);
        userService.update(userDTO);
    }

    @PostMapping(path = "/update-address")
    public void updateAddress(@RequestBody UserDTO userDTO) {
        ValidatorError validatorError = userValidation.validateUserUpdateAddress(userDTO);
        if (!validatorError.getErrors().isEmpty())
            throw new ValidationException(validatorError);
        userService.updateAddress(userDTO);
    }

    @PostMapping(path = "/change/password")
    public void changePassword(@RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
    }
}
