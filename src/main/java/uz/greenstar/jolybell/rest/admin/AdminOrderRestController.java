package uz.greenstar.jolybell.rest.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.filterForm.FilterRequest;
import uz.greenstar.jolybell.api.filterForm.FilterResponse;
import uz.greenstar.jolybell.service.OrderService;

@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class AdminOrderRestController {
    private final OrderService orderService;

    @PostMapping(path = "/list")
    public FilterResponse list(@RequestBody FilterRequest request) {
        return orderService.listByAdmin(request);
    }

    @GetMapping(path = "/accept/{id}")
    public void accept(@PathVariable("id") String id){
        orderService.accept(id);
    }
}
