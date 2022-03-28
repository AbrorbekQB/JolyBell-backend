package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.greenstar.jolybell.dto.UserDTO;
import uz.greenstar.jolybell.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class UserRestController {
    private final UserService userService;

    @GetMapping("/get")
    public ResponseEntity<UserDTO> get(HttpServletRequest request) {
        return ResponseEntity.ok(userService.get(request.getHeader("userId")));
    }
}
