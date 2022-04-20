package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.api.order.OrderItemRemove;
import uz.greenstar.jolybell.dto.OrderDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.ProductCountEntity;
import uz.greenstar.jolybell.entity.ProductEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.exception.OrderNotFoundException;
import uz.greenstar.jolybell.exception.ProductNotFoundException;
import uz.greenstar.jolybell.exception.product.ProductAlreadySoldException;
import uz.greenstar.jolybell.repository.OrderRepository;
import uz.greenstar.jolybell.repository.ProductCountRepository;
import uz.greenstar.jolybell.repository.ProductRepository;
import uz.greenstar.jolybell.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductCountRepository productCountRepository;

    public String create(OrderItem orderItem) {
        OrderEntity orderEntity = new OrderEntity();

        Optional<ProductCountEntity> productCountEntityOptional = productCountRepository.findByProductAndSize(productRepository.getById(orderItem.getProductId()), orderItem.getSize());
        ProductCountEntity productCountEntity = productCountEntityOptional.get();

        if (productCountEntity.getCount() <= 0)
            throw new ProductAlreadySoldException();

        if (productCountEntity.getCount() < orderItem.getCount())
            orderItem.setCount(productCountEntity.getCount());

        orderItem.setProductCountId(productCountEntity.getId());
        orderEntity.getOrderItems().put(orderItem.getProductId(), List.of(orderItem));
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        userOptional.ifPresent(orderEntity::setUser);

        orderEntity.setTotalAmount(BigDecimal.ZERO.add(orderItem.getCost()).multiply(BigDecimal.valueOf(orderItem.getCount())));
        return orderRepository.save(orderEntity).getId();
    }

    public void update(String orderId, OrderItem orderItem) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        Map<String, List<OrderItem>> orderItems = updateOrder(optionalOrder.get().getOrderItems(), orderItem);

        BigDecimal amount = optionalOrder.get().getTotalAmount()
                .add(orderItem.getCost().multiply(BigDecimal.valueOf(orderItem.getCount())));
        optionalOrder.get().setTotalAmount(amount);
        optionalOrder.get().setOrderItems(orderItems);
        optionalOrder.get().setLastUpdateTime(LocalDateTime.now());
        orderRepository.save(optionalOrder.get());
    }

    private Map<String, List<OrderItem>> updateOrder(Map<String, List<OrderItem>> orderItems, OrderItem orderItem) {

        Map<String, ProductCountEntity> productCountEntityMap = productCountRepository.findAllByProduct(productRepository.getById(orderItem.getProductId()))
                .stream().collect(Collectors.toMap(ProductCountEntity::getSize, o -> o));

        if (!orderItems.containsKey(orderItem.getProductId())) {
            ProductCountEntity productCountEntity = productCountEntityMap.get(orderItem.getSize());

            if (productCountEntity.getCount() <= 0)
                throw new ProductAlreadySoldException();

            if (productCountEntity.getCount() < orderItem.getCount())
                orderItem.setCount(productCountEntity.getCount());

            orderItem.setProductCountId(productCountEntity.getId());
            orderItems.put(orderItem.getProductId(), List.of(orderItem));
            return orderItems;
        }

        List<OrderItem> currentOrderItems = orderItems.get(orderItem.getProductId());
        List<String> sizeList = currentOrderItems.stream().map(OrderItem::getSize).collect(Collectors.toList());

        if (!sizeList.contains(orderItem.getSize())) {
            ProductCountEntity productCountEntity = productCountEntityMap.get(orderItem.getSize());
            if (productCountEntity.getCount() <= 0)
                throw new ProductAlreadySoldException();

            if (productCountEntity.getCount() < orderItem.getCount())
                orderItem.setCount(productCountEntity.getCount());

            orderItem.setProductCountId(productCountEntity.getId());
            orderItems.get(orderItem.getProductId()).add(orderItem);
            return orderItems;
        }

        currentOrderItems.forEach(currentOrderItem -> {
            if (currentOrderItem.getSize().equals(orderItem.getSize())) {
                ProductCountEntity productCountEntity = productCountEntityMap.get(orderItem.getSize());

                if (productCountEntity.getCount() <= 0)
                    throw new ProductAlreadySoldException();

                long totalCount = currentOrderItem.getCount() + orderItem.getCount();

                if (productCountEntity.getCount() < totalCount)
                    currentOrderItem.setCount(productCountEntity.getCount());
                else
                    currentOrderItem.setCount(totalCount);
            }
        });

        return orderItems;
    }

    public OrderDTO getById(String id, OrderStatus status) {
        Optional<OrderEntity> orderOptional = orderRepository.findByIdAndStatus(id, status);
        if (orderOptional.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        OrderEntity orderEntity = orderOptional.get();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(orderEntity.getId());
        orderDTO.setTotalAmount(orderEntity.getTotalAmount());

        Set<String> productIdList = new HashSet<>();
        orderEntity.getOrderItems().forEach((productId, orderItems) -> {
            productIdList.add(productId);
        });

        Map<String, String> map = productRepository.findAllByIdIn(productIdList).stream().collect(Collectors.toMap(
                ProductEntity::getId, productEntity -> productEntity.getImageItems().get(0).getUrl()
        ));

        List<OrderItem> orderItems = new ArrayList<>();
        orderEntity.getOrderItems().forEach((productId, orderItems1) -> {
            orderItems1.forEach(orderItem -> {
                orderItem.setUrl(map.get(orderItem.getProductId()));
            });
            orderItems.addAll(orderItems1);
        });
        orderDTO.setOrderItems(orderItems);
        return orderDTO;
    }

    public void removeItem(OrderItemRemove orderItemRemove) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(orderItemRemove.getOrderId());
        if (optionalOrder.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        Map<String, List<OrderItem>> map = optionalOrder.get().getOrderItems();
        if (!map.containsKey(orderItemRemove.getProductId()))
            throw new ProductNotFoundException("Product not found!");

        List<OrderItem> orderItemList = new ArrayList<>(map.get(orderItemRemove.getProductId()));

        map.get(orderItemRemove.getProductId()).forEach(orderItem -> {
            if (orderItem.getId().equals(orderItemRemove.getOrderItemId())) {
                orderItemList.remove(orderItem);
                BigDecimal removeOrderItemAmount = orderItem.getCost().multiply(BigDecimal.valueOf(orderItem.getCount()));
                optionalOrder.get().setTotalAmount(optionalOrder.get().getTotalAmount().subtract(removeOrderItemAmount));
            }
        });
        map.put(orderItemRemove.getProductId(), orderItemList);
        optionalOrder.get().setOrderItems(map);
        optionalOrder.get().setLastUpdateTime(LocalDateTime.now());
        orderRepository.save(optionalOrder.get());
    }

    public void confirm(String orderId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        OrderEntity orderEntity = orderOptional.get();
        orderEntity.setStatus(OrderStatus.CONFIRM);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount().add(BigDecimal.valueOf(5.0)));
        orderEntity.setLastUpdateTime(LocalDateTime.now());
        orderEntity.setUser(userOptional.get());
        orderRepository.save(orderEntity);
    }

    public String getAmount(String id) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(id);
        return orderEntityOptional.get().getTotalAmount().toString();
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
