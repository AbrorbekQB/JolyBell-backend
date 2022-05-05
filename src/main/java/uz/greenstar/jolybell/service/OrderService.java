package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.api.order.OrderData;
import uz.greenstar.jolybell.api.order.OrderItem;
import uz.greenstar.jolybell.api.order.OrderItemRemove;
import uz.greenstar.jolybell.dto.order.ChoicePaymentDTO;
import uz.greenstar.jolybell.dto.order.OrderGetDTO;
import uz.greenstar.jolybell.dto.order.OrderDTO;
import uz.greenstar.jolybell.dto.order.CheckoutOrderDTO;
import uz.greenstar.jolybell.entity.*;
import uz.greenstar.jolybell.enums.OrderState;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.enums.PaymentType;
import uz.greenstar.jolybell.enums.ReservedProductStatus;
import uz.greenstar.jolybell.exception.OrderNotFoundException;
import uz.greenstar.jolybell.exception.ProductNotFoundException;
import uz.greenstar.jolybell.exception.product.ProductAlreadySoldException;
import uz.greenstar.jolybell.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductCountRepository productCountRepository;
    private final ReservedProductRepository reservedProductRepository;
    private final ProductService productService;

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

    public OrderDTO getById(OrderGetDTO getOrderDTO) {
        Optional<OrderEntity> orderOptional = orderRepository.findByIdAndStatus(getOrderDTO.getId(), getOrderDTO.getStatus());
        if (orderOptional.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        AtomicReference<Boolean> fullReserver = new AtomicReference<>(Boolean.TRUE);
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
                if (orderItem.getCount() != orderItem.getReservedCount())
                    fullReserver.set(Boolean.FALSE);
            });
            orderItems.addAll(orderItems1);
        });
        orderDTO.setOrderItems(orderItems);
        orderDTO.setFullReserved(fullReserver.get());
        if (Objects.nonNull(orderEntity.getBookedDateTime()))
            orderDTO.setOrderLifeTime(orderEntity.getBookedDateTime().atZone(ZoneId.systemDefault()).toEpochSecond() - LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond());
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

    public boolean confirm(String orderId) {
        AtomicBoolean fullReserved = new AtomicBoolean(true);
        AtomicReference<BigDecimal> finaltotalAmount = new AtomicReference<>(BigDecimal.ZERO);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty() || !orderOptional.get().getStatus().equals(OrderStatus.PENDING))
            throw new OrderNotFoundException("Order not found!");

        OrderEntity orderEntity = orderOptional.get();
        orderEntity.setUser(userOptional.get());

        orderEntity.getOrderItems().forEach((productId, orderItems) -> {
            orderItems.forEach(orderItem -> {
                Long reservedCount = productService.reservingProduct(orderItem.getProductCountId(), orderItem.getCount(), orderEntity.getId());
                if (reservedCount.equals(orderItem.getCount()))
                    orderItem.setReservedCount(orderItem.getCount());
                else {
                    fullReserved.set(false);
                    orderItem.setReservedCount(reservedCount);
                }

                finaltotalAmount.set(finaltotalAmount.get().add(orderItem.getCost().multiply(BigDecimal.valueOf(reservedCount))));
            });
        });

        orderEntity.setStatus(OrderStatus.CONFIRM);
        orderEntity.setTotalAmount(orderEntity.getTotalAmount().add(BigDecimal.valueOf(5.0)));
        orderEntity.setLastUpdateTime(LocalDateTime.now());
        orderEntity.setFullReserved(fullReserved.get());
        orderEntity.setBookedDateTime(LocalDateTime.now().plusMinutes(10));
        orderEntity.setUser(userOptional.get());
        orderEntity.setTotalAmount(finaltotalAmount.get());
        orderRepository.save(orderEntity);

        return fullReserved.get();
    }

    public String getAmount(String id) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(id);
        return orderEntityOptional.get().getTotalAmount().toString();
    }

    public void cancelOrder(String id) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty() || optionalOrder.get().getStatus().equals(OrderStatus.PENDING))
            return;

        OrderEntity orderEntity = optionalOrder.get();

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!orderEntity.getUser().getUsername().equals(username)) {
            return;
        }

        orderEntity.setStatus(OrderStatus.CANCELED);
        orderEntity.setLastUpdateTime(LocalDateTime.now());
        orderRepository.save(orderEntity);

        List<ReservedProductEntity> reservedProductEntityList = reservedProductRepository.findAllByOrderIdAndStatus(orderEntity.getId(), ReservedProductStatus.RESERVED);
        productService.returnReservedProduct(reservedProductEntityList);
    }

    public boolean checkout(CheckoutOrderDTO checkout) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(checkout.getOrderId());
        if (optionalOrder.isEmpty() || !optionalOrder.get().getStatus().equals(OrderStatus.CONFIRM))
            return false;

        OrderEntity orderEntity = optionalOrder.get();
        orderEntity.setNote(checkout.getNote());
        orderEntity.setReceiverDetails(checkout.getUser());
        orderEntity.setStatus(OrderStatus.CHECKOUT);
        orderEntity.setLastUpdateTime(LocalDateTime.now());
        orderRepository.save(orderEntity);
        return true;
    }

    public void choicePayment(ChoicePaymentDTO dto) {
        Optional<OrderEntity> optionalOrder = orderRepository.findById(dto.getOrderId());
        if (optionalOrder.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        OrderEntity orderEntity = optionalOrder.get();
        orderEntity.setTotalAmount(orderEntity.getTotalAmount().add(new BigDecimal(5)));
        orderEntity.setStatus(OrderStatus.CHOICE_PAYMENT);
        orderEntity.setPaymentType(PaymentType.PAYSYS);
        orderEntity.setLastUpdateTime(LocalDateTime.now());
        orderRepository.save(orderEntity);
    }

    public List<OrderData> list() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        List<OrderEntity> orderEntities = orderRepository.findAllByUserAndStateIsNotOrderByCreatedDateTimeDesc(userOptional.get(), OrderState.PENDING);
        return orderEntities.stream().map(orderEntity -> {
            OrderData orderData = new OrderData();
            orderData.setState(orderEntity.getState());
            orderData.setId(orderEntity.getId());
            orderData.setCreatedDate(orderEntity.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            List<OrderItem> orderItems = new ArrayList<>();
            orderEntity.getOrderItems().forEach((productId, orderItems1) -> {
                orderItems.addAll(orderItems1);
            });

            orderData.setOrderItems(orderItems);
            orderData.setTotalAmount(orderEntity.getTotalAmount());
            return orderData;
        }).collect(Collectors.toList());
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
