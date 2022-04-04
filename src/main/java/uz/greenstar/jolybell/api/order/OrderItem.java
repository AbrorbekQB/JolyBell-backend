package uz.greenstar.jolybell.api.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderItem {
    private String id = UUID.randomUUID().toString();
    private String productId;
    private String name = "Abror";
    private String url;
    private int count;
    private BigDecimal cost = BigDecimal.ZERO;
    private String size;
}
