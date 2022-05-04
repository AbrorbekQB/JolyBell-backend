package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.greenstar.jolybell.api.payment.PaySysResponse;
import uz.greenstar.jolybell.dto.payment.PaySysPayDTO;
import uz.greenstar.jolybell.service.PaySysService;

@RestController
@RequestMapping("/paysys")
@RequiredArgsConstructor
public class PaySysRestController {
    private final PaySysService paySysService;

    @PostMapping(path = "/pay")
    public PaySysResponse pay(@RequestBody PaySysPayDTO dto) {
        return paySysService.pay(dto);
    }
}
