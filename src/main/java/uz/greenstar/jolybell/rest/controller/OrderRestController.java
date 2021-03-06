package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.api.order.OrderData;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.api.order.OrderItemRemove;
import uz.greenstar.jolybell.dto.order.ChoicePaymentDTO;
import uz.greenstar.jolybell.dto.order.OrderGetDTO;
import uz.greenstar.jolybell.dto.order.OrderDTO;
import uz.greenstar.jolybell.dto.order.CheckoutOrderDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
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

    @GetMapping("/amount/{id}")
    public String amount(@PathVariable("id") String id) {
        return orderService.getAmount(id);
    }

    @GetMapping("/list")
    public List<OrderData> list(){
        return orderService.list();
    }

    @PostMapping(path = "/get")
    public OrderDTO get(@RequestBody OrderGetDTO getOrderDTO) {
        return orderService.getById(getOrderDTO);
    }

    @GetMapping(path = "/cancel/{id}")
    public void cancel(@PathVariable("id") String id) {
        orderService.cancelOrder(id);
    }

    @PostMapping(path = "/remove/item")
    public void removeItem(@RequestBody OrderItemRemove orderItemRemove) {
        orderService.removeItem(orderItemRemove);
    }

    @GetMapping(path = "/confirm/{orderId}")
    public boolean checkout(@PathVariable("orderId") String orderId) {
        return orderService.confirm(orderId);
    }

    @PostMapping(path = "/checkout")
    public boolean proceed(@RequestBody CheckoutOrderDTO checkoutOrderDTO) {
        return orderService.checkout(checkoutOrderDTO);
    }

    @PostMapping(path = "/choice-payment")
    public void choicePayment(@RequestBody ChoicePaymentDTO dto) {
        orderService.choicePayment(dto);
    }
//
//    @GetMapping("/get/{id}")
//    public OrderDTO get(@PathVariable("id") String id) {
//        return orderService.get(id);
//    }
}