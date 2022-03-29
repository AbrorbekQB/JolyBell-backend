package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.dto.OrderDTO;
import uz.greenstar.jolybell.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class OrderRestController {
    private final OrderService orderService;

    @PostMapping("/create")
    public String create(@RequestBody OrderItem orderItem) {
        return orderService.create(orderItem);
    }

    @PostMapping("/update/{id}")
    public void update(@RequestBody OrderItem orderItem, @PathVariable("id") String orderId) {
        orderService.update(orderId, orderItem);
    }
//
//    @GetMapping("/get/{id}")
//    public OrderDTO get(@PathVariable("id") String id) {
//        return orderService.get(id);
//    }
}