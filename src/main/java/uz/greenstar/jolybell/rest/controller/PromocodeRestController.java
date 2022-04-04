package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.PromocodeDTO;
import uz.greenstar.jolybell.service.PromocodeService;

import java.util.Map;

@RestController
@RequestMapping("/promocode")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class PromocodeRestController {
    private final PromocodeService promocodeService;

    @PostMapping("/check")
    public PromocodeDTO check(@RequestBody Map<String, String> request) {
        return promocodeService.check(request);
    }
}
