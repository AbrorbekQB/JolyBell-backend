package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.dto.OrderDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.exception.OrderNotFoundException;
import uz.greenstar.jolybell.repository.OrderRepository;
import uz.greenstar.jolybell.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public String create(OrderItem orderItem) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.getOrderItems().put(orderItem.getProductId(), orderItem);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new ItemNotFoundException("User not found!");
        orderEntity.setTotalAmount(BigDecimal.ZERO.add(orderItem.getCost()).multiply(BigDecimal.valueOf(orderItem.getCount())));
        orderEntity.setUser(userOptional.get());
        return orderRepository.save(orderEntity).getId();
    }

    public void update(String orderId, OrderItem orderItem) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        Map<String, OrderItem> orderItems = optionalOrder.get().getOrderItems();
        OrderItem currentOrderItem = orderItems.get(orderItem.getProductId());

        if (orderItems.containsKey(orderItem.getProductId()) && !currentOrderItem.getSize().equals(orderItem.getSize())) {
            currentOrderItem.setCost(orderItem.getCost());
            currentOrderItem.setCount(currentOrderItem.getCount() + orderItem.getCount());
            orderItems.put(currentOrderItem.getProductId(), currentOrderItem);
        } else {
            orderItems.put(orderItem.getProductId(), orderItem);
        }
        optionalOrder.get().getTotalAmount().add(
                optionalOrder.get().getTotalAmount()
                        .add(orderItem.getCost().multiply(BigDecimal.valueOf(orderItem.getCount()))));
        optionalOrder.get().setOrderItems(orderItems);
        orderRepository.save(optionalOrder.get());
    }
//    public OrderDTO get(String id) {
//        Optional<OrderEntity> optionalBooking = bookingRepository.findById(id);
//        if (optionalBooking.isPresent()) {
//            OrderDTO orderDTO = new OrderDTO();
//            BeanUtils.copyProperties(optionalBooking.get(), orderDTO);
//            return orderDTO;
//        }
//        return null;
//    }
}
