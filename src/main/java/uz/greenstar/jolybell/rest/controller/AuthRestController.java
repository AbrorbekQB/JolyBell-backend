package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.auth.LoginRequest;
import uz.greenstar.jolybell.api.auth.LoginResponse;
import uz.greenstar.jolybell.api.auth.RegistrationRequest;
import uz.greenstar.jolybell.dto.AuthDTO;
import uz.greenstar.jolybell.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/forgot")
    public void forgot(@RequestBody AuthDTO authDTO) {
        return;
    }

    @PostMapping("/registration")
    public ResponseEntity<LoginResponse> registration(@RequestBody RegistrationRequest registration){
        return ResponseEntity.ok(authService.registration(registration));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}