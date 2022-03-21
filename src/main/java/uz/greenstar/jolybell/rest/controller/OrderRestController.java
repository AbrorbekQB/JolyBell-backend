package uz.greenstar.jolybell.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.greenstar.jolybell.dto.BookingDTO;
import uz.greenstar.jolybell.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200")
public class OrderRestController {
    private final OrderService bookingService;

    @PostMapping("/create")
    public String create(@RequestBody BookingDTO bookingDTO) {
        return bookingService.create(bookingDTO);
    }

    @PostMapping("/update")
    public void update(@RequestBody BookingDTO bookingDTO) {
        bookingService.update(bookingDTO);
    }

    @GetMapping("/get/{id}")
    public BookingDTO get(@PathVariable("id") String id) {
        return bookingService.get(id);
    }
}