package uz.greenstar.jolybell.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.greenstar.jolybell.entity.OrderEntity;
import uz.greenstar.jolybell.entity.ProvinceEntity;
import uz.greenstar.jolybell.entity.UserEntity;
import uz.greenstar.jolybell.enums.OrderStatus;
import uz.greenstar.jolybell.repository.OrderRepository;
import uz.greenstar.jolybell.repository.ProvinceRepository;
import uz.greenstar.jolybell.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProvinceRepository provinceRepository;

    public Map reportUser() {
        Map<String, Object> response = new HashMap<>();
        List<UserEntity> userEntityList = userRepository.findAll();

        Map<String, Object> userCount = new HashMap<>();

        AtomicInteger activeUsers = new AtomicInteger();
        AtomicInteger inactiveUsers = new AtomicInteger();
        userEntityList.forEach(user -> {
            if (user.getActive())
                activeUsers.getAndIncrement();
            else
                inactiveUsers.getAndIncrement();
        });
        userCount.put("all", userEntityList.size());
        userCount.put("active", activeUsers.get());
        userCount.put("inactive", inactiveUsers.get());
        response.put("userCount", userCount);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNow = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0, 0);
        LocalDateTime endNow = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 23, 59, 59);

        Map<String, Object> userDailyReport = new HashMap<>();
        List<String> date = new ArrayList<>();
        List<Long> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LocalDateTime start = startNow.minusDays(i);
            LocalDateTime end = endNow.minusDays(i);

            data.add(userEntityList.stream()
                    .filter(user ->
                            user.getCreateTime().isAfter(start) && user.getCreateTime().isBefore(end))
                    .count());
            date.add(start.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)));
        }

        userDailyReport.put("date", date);
        userDailyReport.put("data", data);
        response.put("userDaily", userDailyReport);

        return response;
    }

    public Map reportOrder() {
        Map<String, Object> response = new HashMap<>();

        List<OrderEntity> orderEntityList = orderRepository.findAll();

        List<ProvinceEntity> provinceEntityList = provinceRepository.findAll();

        Map<String, Object> provinceDailyReport = new HashMap<>();
        Map<String, String> provinceList = new HashMap<>();
        AtomicReference<BigDecimal> totalAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<Integer> userFinish = new AtomicReference<>(0);
        AtomicReference<Integer> warehouse = new AtomicReference<>(0);
        AtomicReference<Integer> supplier = new AtomicReference<>(0);
        AtomicReference<Integer> finished = new AtomicReference<>(0);

        provinceEntityList.forEach(provinceEntity -> {
            provinceDailyReport.put(provinceEntity.getId(), 0);
            provinceList.put(provinceEntity.getId(), provinceEntity.getName());
        });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startNow = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0, 0);
        LocalDateTime endNow = LocalDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 23, 59, 59);

        Map<String, Object> orderDailyReport = new HashMap<>();
        Map<String, Object> moneyDailyReport = new HashMap<>();

        List<String> date = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        List<BigDecimal> dataMoney = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LocalDateTime start = startNow.minusDays(i);
            LocalDateTime end = endNow.minusDays(i);

            AtomicInteger count = new AtomicInteger();
            AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);
            orderEntityList.stream()
                    .filter(order ->
                            order.getStatus().equals(OrderStatus.PAY_SUCCESS)
                                    && order.getBookedDateTime().isAfter(start)
                                    && order.getBookedDateTime().isBefore(end))
                    .forEach(order -> {
                        amount.set(amount.get().add(order.getTotalAmount()));
                        totalAmount.set(totalAmount.get().add(order.getTotalAmount()));
                        switch (order.getState()) {
                            case USER_FINISH:
                                userFinish.getAndSet(userFinish.get() + 1);
                                break;
                            case WAREHOUSE:
                                warehouse.getAndSet(warehouse.get() + 1);
                                break;
                            case SUPPLIER:
                                supplier.getAndSet(warehouse.get() + 1);
                                break;
                            case FINISHED:
                                finished.getAndSet(finished.get() + 1);
                                break;
                        }
                        count.getAndIncrement();
                        provinceDailyReport.put(order.getReceiverDetails().getProvince(), (int) provinceDailyReport.get(order.getReceiverDetails().getProvince()) + 1);
                    });
            data.add(count.get());
            dataMoney.add(amount.get());
            date.add(start.format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)));
        }

        List<String> provinceNameList = new ArrayList<>();
        List<Integer> orderCountByProvince = new ArrayList<>();
        provinceDailyReport.forEach((s, integer) -> {
            provinceNameList.add(provinceList.get(s));
            orderCountByProvince.add((Integer) provinceDailyReport.get(s));
        });

        orderDailyReport.put("date", date);
        orderDailyReport.put("data", data);

        moneyDailyReport.put("data", dataMoney);
        moneyDailyReport.put("date", date);
        moneyDailyReport.put("totalAmount", totalAmount);

        provinceDailyReport.clear();
        provinceDailyReport.put("name", provinceNameList);
        provinceDailyReport.put("data", orderCountByProvince);

        response.put("userFinish", userFinish.get());
        response.put("warehouse", warehouse.get());
        response.put("supplier", supplier.get());
        response.put("finished", finished.get());
        response.put("orderDaily", orderDailyReport);
        response.put("moneyDaily", moneyDailyReport);
        response.put("provinceDaily", provinceDailyReport);

        return response;
    }
}
