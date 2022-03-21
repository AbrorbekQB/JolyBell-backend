package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.AuthDTO;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class AuthRestController {
    @PostMapping("/forgot")
    public void forgot(@RequestBody AuthDTO authDTO){
        return ;
    }
}
