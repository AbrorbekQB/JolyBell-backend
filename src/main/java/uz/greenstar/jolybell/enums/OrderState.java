package uz.greenstar.jolybell.enums;

import java.util.Arrays;

public enum OrderState {
    PENDING, USER_FINISH, WAREHOUSE, SUPPLIER, NONE, FINISHED;

    public static OrderState find(String state) {
        return Arrays.stream(OrderState.values())
                .filter(e -> e.toString().toLowerCase().contains(state.toLowerCase()))
                .findAny()
                .orElse(OrderState.NONE);
    }
}
