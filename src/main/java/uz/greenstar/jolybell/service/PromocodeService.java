package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.dto.PromocodeDTO;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.PromoCodeEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.exception.ItemNotFoundException;
import uz.greenstar.jolybell.exception.OrderNotFoundException;
import uz.greenstar.jolybell.exception.PromoCodeCreatingException;
import uz.greenstar.jolybell.repository.OrderRepository;
import uz.greenstar.jolybell.repository.PromocodeRepository;
import uz.greenstar.jolybell.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromocodeService {
    private final PromocodeRepository promocodeRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public void create(PromocodeDTO dto) {
        PromoCodeEntity promoCodeEntity = new PromoCodeEntity();

        Optional<PromoCodeEntity> promoCodeOptional = promocodeRepository.findByCode(dto.getCode());
        if (promoCodeOptional.isPresent())
            throw new PromoCodeCreatingException("Promocode already exist");

        promoCodeEntity.setCode(dto.getCode());
        promoCodeEntity.setCreateDateTime(LocalDateTime.now());
        promoCodeEntity.setLastUpdateTime(LocalDateTime.now());
        promoCodeEntity.setActive(true);
        promoCodeEntity.setStartDate(dto.getStartDate());
        promoCodeEntity.setEndDate(dto.getEndDate());
        promoCodeEntity.setPercent(dto.getPercent());
        promocodeRepository.save(promoCodeEntity);
    }

    public PromocodeDTO check(Map request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        PromocodeDTO promocodeDTO = new PromocodeDTO();
        promocodeDTO.setActive(false);
        promocodeDTO.setCode(request.get("code").toString());

        Optional<UserEntity> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new UsernameNotFoundException("User not found!");

        Optional<OrderEntity> orderOptional = orderRepository.findByIdAndStatus((String) request.get("orderId"), OrderStatus.PENDING);
        if (orderOptional.isEmpty())
            throw new OrderNotFoundException("Order not found!");

        Optional<PromoCodeEntity> promocodeOptional = promocodeRepository.findByCode(request.get("code").toString());
        if (promocodeOptional.isEmpty())
            throw new ItemNotFoundException("Promocode not found!");

        if (orderRepository.findByUserAndPromoCode(userOptional.get(), promocodeOptional.get()).isPresent())
            return promocodeDTO;

        if (Objects.nonNull(orderOptional.get().getPromoCode()) || promocodeOptional.get().getEndDate().isBefore(LocalDate.now()))
            return promocodeDTO;

        orderOptional.get().setPromoCode(promocodeOptional.get());
        orderOptional.get().setUser(userOptional.get());
        orderRepository.save(orderOptional.get());

        promocodeDTO.setActive(true);
        return promocodeDTO;
    }
}
