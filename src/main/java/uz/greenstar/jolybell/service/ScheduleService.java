package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.greenstar.jolybell.entity.ReservedProductEntity;
import uz.greenstar.jolybell.enums.ReservedProductStatus;
import uz.greenstar.jolybell.repository.ReservedProductRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ReservedProductRepository reservedProductRepository;
    private final ProductService productService;

    @Scheduled(cron = "*/20 * * * * ?")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void returnReservedProduct() {
        List<ReservedProductEntity> reservedProductEntityList = reservedProductRepository.findTop10ByStatusAndCreateDateBefore(ReservedProductStatus.RESERVED, LocalDateTime.now().minusMinutes(10));
        productService.returnReservedProduct(reservedProductEntityList);
    }
}
