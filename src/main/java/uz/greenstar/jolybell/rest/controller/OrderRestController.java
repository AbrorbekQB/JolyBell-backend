package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.api.order.OrderItemRemove;
import uz.greenstar.jolybell.dto.OrderDTO;
import uz.greenstar.jolybell.enums.OrderStatus;
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

    @GetMapping(path = "/{id}")
    public OrderDTO get(@PathVariable("id") String id) {
        return orderService.getById(id, OrderStatus.PENDING);
    }

    @PostMapping(path = "/remove/item")
    public void removeItem(@RequestBody OrderItemRemove orderItemRemove) {
        orderService.removeItem(orderItemRemove);
    }

    @GetMapping(path = "/confirm/{orderId}")
    public void checkout(@PathVariable("orderId") String orderId) {
        orderService.confirm(orderId);
    }
//
//    @GetMapping("/get/{id}")
//    public OrderDTO get(@PathVariable("id") String id) {
//        return orderService.get(id);
//    }
}