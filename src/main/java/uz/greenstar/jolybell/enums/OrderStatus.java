package uz.greenstar.jolybell.enums;

import java.util.Arrays;

public enum OrderStatus {
    NONE,
    PENDING,
    CONFIRM,
    CHECKOUT,
    CANCELED,
    PAID,
    CANCELED_BY_EXPIRED,
    CHOICE_PAYMENT,
    PAY_SUCCESS,
    PAY_FAIL;

    public static OrderStatus find(String status) {
        return Arrays.stream(OrderStatus.values())
                .filter(e -> e.toString().equalsIgnoreCase(status))
                .findAny()
                .orElse(OrderStatus.NONE);
    }
}
