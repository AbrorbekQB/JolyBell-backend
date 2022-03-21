package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.BookingDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.repository.OrderRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository bookingRepository;

    public String create(BookingDTO bookingDTO) {
        OrderEntity bookingEntity = new OrderEntity();
        bookingEntity.setCount(bookingDTO.getCount());
        bookingEntity.setCost(bookingDTO.getCost());
        bookingEntity.setProductId(bookingDTO.getProductId());
        bookingRepository.save(bookingEntity);
        return bookingEntity.getId();
    }

    public void update(BookingDTO bookingDTO) {
        Optional<OrderEntity> optionalBooking = bookingRepository.findById(bookingDTO.getId());
        optionalBooking.get().setCost(optionalBooking.get().getCost().add(bookingDTO.getCost()));
        optionalBooking.get().setCount(optionalBooking.get().getCount() + bookingDTO.getCount());
        bookingRepository.save(optionalBooking.get());
    }

    public BookingDTO get(String id) {
        Optional<OrderEntity> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            BookingDTO bookingDTO = new BookingDTO();
            BeanUtils.copyProperties(optionalBooking.get(), bookingDTO);
            return bookingDTO;
        }
        return null;
    }
}
