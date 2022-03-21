package uz.greenstar.jolybell.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BookingDTO {
    private String id;
    private String productId;
    private long count;
    private BigDecimal cost;
}
