package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.service.UserService;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class UserRestController {
    private final UserService userService;
}
